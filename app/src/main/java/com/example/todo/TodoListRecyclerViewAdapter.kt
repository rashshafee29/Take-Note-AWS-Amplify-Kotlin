package com.example.todo

import android.content.Context
import android.graphics.DrawFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo
import kotlin.coroutines.coroutineContext

class TodoListRecyclerViewAdapter(private var context: Context, private var mTodoList: ArrayList<Todo>) : RecyclerView.Adapter<TodoListRecyclerViewAdapter.TodoListViewHolder>() {

    class TodoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitle: TextView = itemView.findViewById(R.id.id_todo_title)
        val todoPriority: TextView = itemView.findViewById(R.id.id_todo_priority)
        val todoDescription: TextView = itemView.findViewById(R.id.id_todo_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.base_list_item, parent, false)
        return TodoListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.todoTitle.text = mTodoList[position].name.toString()

        val drawable: Drawable = ContextCompat.getDrawable(context, R.drawable.priority_bg)!!
        when(mTodoList[position].priority){
            Priority.HIGH -> {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.high_pr), PorterDuff.Mode.SRC_IN)
                holder.todoPriority.background = drawable
            }
            Priority.NORMAL -> {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.normal_pr), PorterDuff.Mode.SRC_IN)
                holder.todoPriority.background = drawable
            }
            Priority.LOW -> {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.low_pr), PorterDuff.Mode.SRC_IN)
                holder.todoPriority.background = drawable
            }
            else -> {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.no_pr), PorterDuff.Mode.SRC_IN)
                holder.todoPriority.background = drawable
            }
        }
        if (mTodoList[position].priority == null) {
            holder.todoPriority.text = "no priority"
        } else {
            holder.todoPriority.text = mTodoList[position].priority.toString()
        }
        holder.todoDescription.text = mTodoList[position].description.toString()
    }

    override fun getItemCount(): Int {
        return mTodoList.size
    }

    fun updateData(todo: Todo) {
        mTodoList.add(todo)
        notifyItemInserted(mTodoList.size-1)
    }

}