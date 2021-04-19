package ru.yodata.app65.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactListBinding
import ru.yodata.app65.utils.Constants.TAG
import ru.yodata.app65.viewmodel.ContactListViewModel

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {

    private var listFrag: FragmentContactListBinding? = null
    val contactListViewModel: ContactListViewModel by viewModels()
    private var navigateCallback: OnContactListCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactListCallback) {
            navigateCallback = context
        } else throw ClassCastException(
            context.toString() +
                    " must implement OnContactListCallback!"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFrag = FragmentContactListBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.contact_list_fragment_title)
        setHasOptionsMenu(true)
        val contactListAdapter = ContactListAdapter { contactId ->
            navigateCallback?.navigateToContactDetailsFragment(contactId)
        }
        listFrag?.contactsRecyclerView?.apply {
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        val divider = getDrawable(requireContext(), R.drawable.divider14)
        if (divider != null) {
            listFrag?.contactsRecyclerView?.addItemDecoration(ContactItemDecoration(divider))
        }
        contactListViewModel.getFilteredContactList().observe(viewLifecycleOwner, { contactList ->
            if (!contactList.isNullOrEmpty()) {
                Log.d(TAG, "ContactListFragment обзервер сработал")
                try {
                    contactListAdapter.submitList(contactList)
                } catch (e: IllegalStateException) {
                    Log.d(
                        TAG,
                        "Исключение IllegalStateException в ${this::class.java.simpleName}:" +
                                "${object {}.javaClass.enclosingMethod.name}"
                    )
                    Log.d(TAG, e.stackTraceToString())
                }
            } else
                Toast.makeText(
                    context,
                    getString(R.string.contacts_not_found_msg),
                    Toast.LENGTH_LONG
                ).show()
        })
        contactListViewModel.isContactListLoading().observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                listFrag?.progressBar?.visibility = View.VISIBLE
            } else {
                listFrag?.progressBar?.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
        val searchItem = menu.findItem(R.id.searchView)
        val searchView = searchItem.actionView as SearchView
        // Восстановить значение в поисковой строке, которое автоматически сбрасывается при
        // повороте экрана или переходе на другой фрагмент
        val savedFilter = contactListViewModel.currentFilterValue
        if (!savedFilter.isNullOrBlank()) {
            searchView.run {
                setQuery(savedFilter, false)
                isIconified = false
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchItem.collapseActionView()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactListViewModel.setNameFilter(newText ?: "")
                return true
            }
        })
    }

    override fun onDestroyView() {
        listFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        navigateCallback = null
        super.onDetach()
    }
}