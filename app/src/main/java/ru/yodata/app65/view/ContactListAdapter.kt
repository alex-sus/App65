package ru.yodata.app65.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.yodata.app65.R
import ru.yodata.app65.databinding.ContactListRowBinding
import ru.yodata.app65.model.BriefContact

class ContactListAdapter(
        private val navigateTo: (String) -> Unit
) : ListAdapter<BriefContact, ContactListAdapter.ContactViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactViewHolder(
            contactRow = ContactListRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            ),
            navigateTo = navigateTo
    )

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContactViewHolder(
            private val contactRow: ContactListRowBinding,
            private val navigateTo: (String) -> Unit
    ) : RecyclerView.ViewHolder(contactRow.root) {

        fun bind(item: BriefContact) = with(contactRow) {
            nameTv.text = item.name
            phoneTv.text = item.phone
            if (!item.photoUri.isNullOrEmpty()) {
                photoIv.setImageURI(item.photoUri.toUri())
            } else {
                photoIv.setImageResource(R.drawable.programmer2_70)
            }
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) navigateTo(item.id)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<BriefContact>() {

        override fun areItemsTheSame(oldItem: BriefContact, newItem: BriefContact) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BriefContact, newItem: BriefContact) =
            oldItem.name == newItem.name && oldItem.phone == newItem.phone
    }
}