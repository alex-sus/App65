package ru.yodata.library.view.contacts

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.yodata.library.R
import ru.yodata.library.databinding.FragmentContactListBinding
import ru.yodata.library.di.HasAppComponent
import ru.yodata.library.utils.Constants.TAG
import ru.yodata.library.utils.injectViewModel
import ru.yodata.library.viewmodel.ContactListViewModel
import javax.inject.Inject

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {

    private var listFrag: FragmentContactListBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactListViewModel: ContactListViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
                ?.getAppComponent()
                ?.plusContactListContainer()
                ?.inject(this)
        contactListViewModel = injectViewModel(viewModelFactory)
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
            listFrag?.progressBar?.visibility = View.GONE
            if (!contactList.isNullOrEmpty()) {
                Log.d(TAG, "ContactListFragment обзервер сработал")
                try {
                    contactListAdapter.submitList(contactList)
                } catch (e: IllegalStateException) {
                    Log.d(TAG, e.stackTraceToString())
                }
            } else
                Toast.makeText(
                    context,
                    getString(R.string.contacts_not_found_msg),
                    Toast.LENGTH_LONG
                ).show()
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
        if (savedFilter.isNotBlank()) {
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