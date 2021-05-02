package ru.yodata.library.view

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView

class ContactItemDecoration(private val dividerDrawable: Drawable) : RecyclerView.ItemDecoration() {

    private val dividerWidth = dividerDrawable.intrinsicWidth
    private val dividerHeight = dividerDrawable.intrinsicHeight

    override fun getItemOffsets(outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            val childAdapterPosition = parent.getChildAdapterPosition(view)
                .let { if (it == RecyclerView.NO_POSITION) return else it }
            // После последнего элемента списка разделитель рисовать не нужно
            if (childAdapterPosition == adapter.itemCount - 1) {
                outRect.bottom = 0
            } else {
                outRect.bottom = dividerHeight
            }
        }
    }

    override fun onDraw(canvas: Canvas,
                        parent: RecyclerView,
                        state: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            parent.children.forEach { view ->
                val childAdapterPosition = parent.getChildAdapterPosition(view)
                        .let { if (it == RecyclerView.NO_POSITION) return else it }
                // После последнего элемента списка разделитель рисовать не нужно
                if (childAdapterPosition != adapter.itemCount - 1) {
                    val left = view.left + (view.width - dividerWidth) / 2 // центрирование
                    val top = view.bottom + view.marginBottom
                    val right = left + dividerWidth
                    val bottom = top + dividerHeight
                    dividerDrawable.bounds = Rect(left, top, right, bottom)
                    dividerDrawable.draw(canvas)
                }
            }
        }
    }
}