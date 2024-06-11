package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.pigsAdapter
import com.example.zufffinalyear.databinding.FragmentPigBinding
import com.example.zufffinalyear.models.Expenseitem
import com.example.zufffinalyear.models.Incomeitem
import com.example.zufffinalyear.models.Pigdetails
import com.example.zufffinalyear.models.Stalldetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PigFragment : Fragment() {

    private var _binding: FragmentPigBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var pigsAdapter: pigsAdapter
    private val pigList = mutableListOf<Pigdetails>()
    private var stallId: String? = null
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        stallId = arguments?.getString("stallNo") // Get stallNo from arguments

        setupRecyclerView()
        fetchPigsData()

        val pigBreeds = resources.getStringArray(R.array.pig_breeds_list).toMutableList()
        pigBreeds.add(0, "All")
        val pigBreedsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pigBreeds)
        binding.autoCompleteTextView.setAdapter(pigBreedsAdapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedBreed = parent.getItemAtPosition(position) as String
            if (selectedBreed == "All") {
                pigsAdapter.showAll()
            } else {
                pigsAdapter.filter(selectedBreed)
            }
        }

        binding.fabAddpig.setOnClickListener {
            val action = PigFragmentDirections.actionPigsFragmentToAddpigFragment(stallId!!)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        pigsAdapter = pigsAdapter(
            pigList,
            onArchiveClickListener = { pig ->
                showArchiveDialog(pig)
            },
            onSellClickListener = { pig ->
                showSellDialog(pig)
            },
            onItemClickListener = { pig ->
                val action = PigFragmentDirections.actionPigsFragmentToProfileFragment(stallId!!, pig.tag_no)
                findNavController().navigate(action)
            }
        )

        binding.rvpigs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pigsAdapter
        }
    }

    private fun showArchiveDialog(pig: Pigdetails) {
        val dialogFragment = ArchiveDialogueFragment(pig) { reason, date, price, notes ->
            archivePig(pig, reason, date, price.toDouble(), notes)
        }
        dialogFragment.show(parentFragmentManager, "ArchivePigDialog")
    }

    private fun archivePig(pig: Pigdetails, reason: String, date: String, price: Double, notes: String) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val archiveDetails = pig.copy(
                reasonForArchiving = reason,
                dateOfEvent = date,
                predictedPrice = price,
                notes = notes
            )

            firestore.collection("users").document(userId).collection("archive")
                .document(pig.tag_no)
                .set(archiveDetails)
                .addOnSuccessListener {
                    removePig(pig)
                    val expenseItem = Expenseitem(description = reason, amount = price, date = date)
                    addExpense(userId, expenseItem)
                    decrementPigCountInStall(userId, stallId!!)
                }
                .addOnFailureListener { e ->
                    Log.e("PigFragment", "Error archiving pig", e)
                    Toast.makeText(requireContext(), "Failed to archive pig.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addExpense(userId: String, expenseItem: Expenseitem) {
        val expenseId = generateNextId()
        firestore.collection("users").document(userId)
            .collection("expenses").document(expenseId)
            .set(expenseItem)
            .addOnSuccessListener {
                Log.d("PigFragment", "Expense item added successfully with ID: $expenseId")
            }
            .addOnFailureListener { e ->
                Log.e("PigFragment", "Error adding expense item", e)
                Toast.makeText(requireContext(), "Failed to add expense item.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addIncome(userId: String, incomeItem: Incomeitem) {
        val incomeId = generateNextId()
        firestore.collection("users").document(userId)
            .collection("income").document(incomeId)
            .set(incomeItem)
            .addOnSuccessListener {
                Log.d("PigFragment", "Income item added successfully with ID: $incomeId")
                // Optionally, refresh the list or take any additional action needed
            }
            .addOnFailureListener { e ->
                Log.e("PigFragment", "Error adding income item", e)
                Toast.makeText(requireContext(), "Failed to add income item.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateNextId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun fetchPigsData() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null && stallId != null) {
            firestore.collection("users").document(userId).collection("stalls").document(stallId!!)
                .collection("pigs")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.e("PigFragment", "Listen failed.", exception)
                        return@addSnapshotListener
                    }

                    pigList.clear()
                    if (snapshot != null && !snapshot.isEmpty) {
                        snapshot.documents.forEach { document ->
                            val pig = document.toObject(Pigdetails::class.java)
                            pig?.let {
                                it.tag_no = document.id
                                pigList.add(it)
                            }
                        }
                    }
                    pigsAdapter.notifyDataSetChanged()
                }
        }
    }

    private fun showSellDialog(pig: Pigdetails) {
        val dialogFragment = DialogueFragment { price ->
            sellPig(pig, price.toDouble())
        }
        dialogFragment.show(parentFragmentManager, "SellPigDialog")
    }

    private fun sellPig(pig: Pigdetails, price: Double) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val saleDetails = Incomeitem(
                description = "Selling pig",
                tagNo = pig.tag_no,
                amount = price,
                date = currentDate
            )

            addIncome(userId, saleDetails)
            removePigAfterSale(pig.tag_no)
            decrementPigCountInStall(userId, stallId!!)
        }
    }

    private fun removePig(pig: Pigdetails) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null && stallId != null) {
            firestore.collection("users").document(userId).collection("stalls").document(stallId!!)
                .collection("pigs").document(pig.tag_no)
                .delete()
                .addOnSuccessListener {
                    Log.d("PigFragment", "Pig removed successfully with ID: ${pig.tag_no}")
                    pigList.remove(pig)
                    pigsAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("PigFragment", "Error removing pig", e)
                    Toast.makeText(requireContext(), "Failed to remove pig.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removePigAfterSale(tagNo: String) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null && stallId != null) {
            firestore.collection("users").document(userId).collection("stalls").document(stallId!!)
                .collection("pigs").document(tagNo)
                .delete()
                .addOnSuccessListener {
                    Log.d("PigFragment", "Pig removed successfully with ID: $tagNo")
                    val pig = pigList.find { it.tag_no == tagNo }
                    pig?.let {
                        pigList.remove(it)
                        pigsAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PigFragment", "Error removing pig", e)
                    Toast.makeText(requireContext(), "Failed to remove pig.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun incrementPigCountInStall(userId: String, stallId: String) {
        val stallReference = database.child("users").child(userId).child("stalls").child(stallId)
        stallReference.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val stall = mutableData.getValue(Stalldetails::class.java)
                    ?: return Transaction.success(mutableData)
                stall.numberOfPigs += 1
                mutableData.value = stall
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                if (databaseError != null) {
                    Log.e("PigFragment", "Error updating pig count", databaseError.toException())
                } else {
                    Log.d("PigFragment", "Pig count updated successfully")
                }
            }
        })
    }

    private fun decrementPigCountInStall(userId: String, stallId: String) {
        val stallReference = database.child("users").child(userId).child("stalls").child(stallId)
        stallReference.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val stall = mutableData.getValue(Stalldetails::class.java)
                    ?: return Transaction.success(mutableData)
                if (stall.numberOfPigs > 0) {
                    stall.numberOfPigs -= 1
                }
                mutableData.value = stall
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                if (databaseError != null) {
                    Log.e("PigFragment", "Error updating pig count", databaseError.toException())
                } else {
                    Log.d("PigFragment", "Pig count updated successfully")
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
