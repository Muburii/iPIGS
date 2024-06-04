package com.example.zufffinalyear.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zufffinalyear.R
import com.example.zufffinalyear.adapter.pigsAdapter
import com.example.zufffinalyear.databinding.FragmentPigBinding
import com.example.zufffinalyear.models.Expenseitem
import com.example.zufffinalyear.models.Pigdetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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

        val piggroup = resources.getStringArray(R.array.pig_group_list).toMutableList()
        piggroup.add(0, "All")
        val piggroupAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, piggroup)
        binding.autoCompletepiggroupTextView.setAdapter(piggroupAdapter)
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
                // Handle the archive button click
                showArchiveDialog(pig)
            },
            onSellClickListener = { pig ->
                // Handle the sell button click
                showSellDialog(pig)
            },
            onItemClickListener = { pig ->
                val bundle = Bundle().apply {
                    putString("documentId", pig.tag_no) // Pass documentId to profileFragment
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
            archivePig(pig, reason, date, price, notes)
        }
        dialogFragment.show(parentFragmentManager, "ArchivePigDialog")
    }

    private fun archivePig(pig: Pigdetails, reason: String, date: String, price: Float, notes: String) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val archiveDetails = pig.copy(
                reasonForArchiving = reason,
                dateOfEvent = date,
                predictedPrice = price,
                notes = notes
            )

            // Add the pig to the archive collection
            firestore.collection("users").document(userId).collection("archive")
                .document(pig.tag_no)
                .set(archiveDetails)
                .addOnSuccessListener {
                    // Remove the pig from the pigs collection
                    removePig(pig)

                    // Add an expense item
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
        firestore.collection("users").document(userId).collection("expenses")
            .add(expenseItem)
            .addOnSuccessListener {
                Log.d("PigFragment", "Expense item added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("PigFragment", "Error adding expense item", e)
            }
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
                                it.tag_no = document.id  // Set the document ID
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
            sellPig(pig, price)
        }
        dialogFragment.show(parentFragmentManager, "SellPigDialog")
    }

    private fun sellPig(pig: Pigdetails, price: Float) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val saleDetails = hashMapOf(
                "description" to "Selling pig",
                "tagNo" to pig.tag_no,  // Use tag_no as the document ID
                "amount" to price,
                "date" to currentDate
            )

            firestore.collection("users").document(userId).collection("income")
                .document(pig.tag_no) // Set the document ID to tag_no
                .set(saleDetails)
                .addOnSuccessListener {
                    val bundle = Bundle().apply {
                        putString("description", "Selling pig")
                        putFloat("amount", price)
                        putString("date", currentDate)
                        putString("documentId", pig.tag_no) // Pass documentId to removePig function
                    }
                    setFragmentResult("sellResult", bundle)
                    removePig(pig)
                }
                .addOnFailureListener { e ->
                    Log.e("PigFragment", "Error adding sale details", e)
                    Toast.makeText(requireContext(), "Failed to record sale.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removePig(pig: Pigdetails) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val pigDocumentPath = "users/$userId/pigs/${pig.tag_no}"
            firestore.document(pigDocumentPath)
                .delete()
                .addOnSuccessListener {
                    Log.d("PigFragment", "Successfully deleted pig with document ID: ${pig.tag_no}")
                    pigList.remove(pig)
                    pigsAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Pig has been deleted from Database.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("PigFragment", "Error deleting pig with document ID: ${pig.tag_no}", e)
                    Toast.makeText(requireContext(), "Failed to delete the pig.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("PigFragment", "User not authenticated. Cannot delete pig.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
