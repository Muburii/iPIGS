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
import com.example.zufffinalyear.models.Pigdetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        setupRecyclerView()
        fetchPigsData()

        val pigBreeds = resources.getStringArray(R.array.pig_breeds_list).toMutableList()
        pigBreeds.add(0, "All")
        val pigBreedsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pigBreeds)
        binding.autoCompleteTextView.setAdapter(pigBreedsAdapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedBreed = parent.getItemAtPosition(position) as String
            val selectedGroup = binding.autoCompletepiggroupTextView.text.toString()
            if (selectedBreed == "All" && selectedGroup == "All") {
                pigsAdapter.showAll()
            } else {
                pigsAdapter.filter(selectedBreed, selectedGroup)
            }
        }

        val pigGroup = resources.getStringArray(R.array.pig_group_list).toMutableList()
        pigGroup.add(0, "All")
        val pigGroupAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pigGroup)
        binding.autoCompletepiggroupTextView.setAdapter(pigGroupAdapter)
        binding.autoCompletepiggroupTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedGroup = parent.getItemAtPosition(position) as String
            val selectedBreed = binding.autoCompleteTextView.text.toString()
            if (selectedBreed == "All" && selectedGroup == "All") {
                pigsAdapter.showAll()
            } else {
                pigsAdapter.filter(selectedBreed, selectedGroup)
            }
        }

        binding.fabAddpig.setOnClickListener {
            findNavController().navigate(R.id.action_pigsFragment_to_addpigFragment)
        }
        binding.rvpigs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pigsAdapter
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
                val bundle = Bundle().apply {
                    putString("documentId", pig.tag_no)
                }
                findNavController().navigate(R.id.action_pigsFragment_to_profileFragment, bundle)
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
                }
                .addOnFailureListener { e ->
                    Log.e("PigFragment", "Error archiving pig", e)
                    Toast.makeText(requireContext(), "Failed to archive pig.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addExpense(userId: String, expenseItem: Expenseitem) {
        expenseItem.month = getMonth(expenseItem.date)
        val expensesRef = firestore.collection("users").document(userId)
            .collection("expenses").document(expenseItem.month)
            .collection("expenseItems")

        expensesRef.get().addOnSuccessListener { snapshot ->
            val nextId = generateNextId(snapshot)
            val newExpenseRef = expensesRef.document(nextId)

            newExpenseRef.set(expenseItem).addOnSuccessListener {
                Log.d("PigFragment", "Expense item added successfully with ID: $nextId")
            }.addOnFailureListener { e ->
                Log.e("PigFragment", "Error adding expense item", e)
                Toast.makeText(requireContext(), "Failed to add expense item.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("PigFragment", "Error fetching existing expense items", e)
            Toast.makeText(requireContext(), "Failed to add expense item.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addIncome(userId: String, incomeItem: Map<String, Any>) {
        val date = incomeItem["date"] as String
        val month = getMonth(date)
        val incomeRef = firestore.collection("users").document(userId)
            .collection("income").document(month)
            .collection("incomeItems")

        incomeRef.get().addOnSuccessListener { snapshot ->
            val nextId = generateNextId(snapshot)
            val newIncomeRef = incomeRef.document(nextId)

            newIncomeRef.set(incomeItem).addOnSuccessListener {
                Log.d("PigFragment", "Income item added successfully with ID: $nextId")
            }.addOnFailureListener { e ->
                Log.e("PigFragment", "Error adding income item", e)
                Toast.makeText(requireContext(), "Failed to add income item.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("PigFragment", "Error fetching existing income items", e)
            Toast.makeText(requireContext(), "Failed to add income item.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateNextId(snapshot: QuerySnapshot): String {
        val existingIds = snapshot.documents.mapNotNull { it.id.toIntOrNull() }
        val nextId = if (existingIds.isEmpty()) 1 else existingIds.maxOrNull()!! + 1
        return nextId.toString().padStart(2, '0')
    }

    private fun fetchPigsData() {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).collection("pigs")
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
            val month = getMonth(currentDate)
            val saleDetails = hashMapOf(
                "description" to "Selling pig",
                "tagNo" to pig.tag_no,
                "amount" to price,
                "date" to currentDate
            )

            val incomeRef = firestore.collection("users").document(userId)
                .collection("income").document(month)
                .collection("incomeItems")

            incomeRef.get().addOnSuccessListener { snapshot ->
                val nextId = generateNextId(snapshot)
                val newIncomeRef = incomeRef.document(nextId)

                newIncomeRef.set(saleDetails).addOnSuccessListener {
                    val bundle = Bundle().apply {
                        putString("description", "Selling pig")
                        putDouble("amount", price)
                        putString("date", currentDate)
                        putString("documentId", pig.tag_no)
                    }
                    removePig(pig)
                    Log.d("PigFragment", "Income item added successfully with ID: $nextId")
                }.addOnFailureListener { e ->
                    Log.e("PigFragment", "Error adding sale details", e)
                    Toast.makeText(requireContext(), "Failed to record sale.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("PigFragment", "Error fetching existing income items", e)
                Toast.makeText(requireContext(), "Failed to fetch income items for ID generation.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removePig(pig: Pigdetails) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).collection("pigs")
                .document(pig.tag_no)
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

    private fun getMonth(date: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)
        val monthFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        return monthFormat.format(parsedDate ?: Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
