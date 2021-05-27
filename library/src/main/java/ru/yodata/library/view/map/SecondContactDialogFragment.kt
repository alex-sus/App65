package ru.yodata.library.view.map

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import ru.yodata.library.R

const val DIALOG_REQUEST_KEY = "dialog"

class SecondContactDialogFragment : DialogFragment() {

    private val nameList: ArrayList<String>? by lazy {
        requireArguments().getStringArrayList(NAME_LIST)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val locatedContactListAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                nameList ?: emptyList()
        )
        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.contact_name_dialog_title)
                    .setAdapter(locatedContactListAdapter) { _, which ->
                        setFragmentResult(
                                DIALOG_REQUEST_KEY,
                                bundleOf(SELECTED_DIALOG_ELEMENT_NUMBER to which)
                        )
                    }
            builder.create()
        }
    }

    companion object {
        private const val NAME_LIST = "nlist"
        val FRAGMENT_NAME: String = SecondContactDialogFragment::class.java.name

        @JvmStatic
        fun newInstance(nameList: ArrayList<String>) =
                SecondContactDialogFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList(NAME_LIST, nameList)
                    }
                }
    }
}