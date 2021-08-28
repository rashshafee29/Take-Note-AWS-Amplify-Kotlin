package com.example.todo

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.TakeNote
import com.amplifyframework.datastore.generated.model.User
import java.util.*
import kotlin.collections.ArrayList

class TodoListRecyclerViewAdapter(private var context: Context, private var mTodoList: ArrayList<TakeNote>)
    : RecyclerView.Adapter<TodoListRecyclerViewAdapter.TodoListViewHolder>(), Filterable {
    private var mFullTodoList: ArrayList<TakeNote>

    private var TAG = "Dota2Same"
    init {
        mFullTodoList = ArrayList(mTodoList)
    }

    class TodoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitle: TextView = itemView.findViewById(R.id.id_todo_title)
        val todoPriority: TextView = itemView.findViewById(R.id.id_todo_priority)
        val todoDescription: TextView = itemView.findViewById(R.id.id_todo_description)
        val editButton: ImageView = itemView.findViewById(R.id.id_edit)
        val deleteButton: ImageView = itemView.findViewById(R.id.id_delete)
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

        holder.editButton.setOnClickListener {
            showEditDialog(context, position)
        }

        holder.deleteButton.setOnClickListener {
            Amplify.DataStore.query(TakeNote::class.java, Where.matches(TakeNote.ID.eq(mTodoList[position].id)),
                { matches ->
                    if (matches.hasNext()) {
                        val todo = matches.next()
                        Amplify.DataStore.delete(todo,
                            { Log.i(TAG, "Deleted a todo.") },
                            { Log.e(TAG, "Delete failed.", it) }
                        )
                    }
                },
                { Log.e(TAG, "Delete Query failed.", it) }
            )
            mTodoList.remove(mTodoList[position])
            notifyItemRemoved(position)
        }
    }

    private fun updateTodo(position: Int, editedTitle: String, editedDesc: String, editedPr: String) {
        var editedTodo: TakeNote
        val priority: Priority? = when (editedPr) {
            "LOW" -> Priority.LOW
            "NORMAL" -> Priority.NORMAL
            "HIGH" -> Priority.HIGH
            else -> null
        }
        val currUser = Amplify.Auth.currentUser
        Amplify.DataStore.query(TakeNote::class.java, Where.matches(TakeNote.ID.eq(mTodoList[position].id)),
            { matches ->
                if (matches.hasNext()) {
                    val todo = matches.next()
                    editedTodo = todo.copyOfBuilder()
                        .name(editedTitle)
                        .description(editedDesc)
                        .priority(priority).user(User.justId(currUser.userId))
                        .build()
                    runOnUiThread{
                        mTodoList[position] = editedTodo
                        notifyItemChanged(position)
                    }
                    Amplify.DataStore.save(editedTodo,
                        { Log.i(TAG, "Edit a todo.") },
                        { Log.e(TAG, "Edit failed.", it) }
                    )
                }
            },
            { Log.e(TAG, "Edit Query failed.", it) }
        )
    }

    private fun showEditDialog(context: Context, position: Int) {
        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_todo_form)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val todoTitle: EditText = dialog.findViewById(R.id.id_task_name)
        val todoDesc: EditText = dialog.findViewById(R.id.id_task_description)
        val todoPriority: Spinner = dialog.findViewById(R.id.id_pr_spinner)
        val errorText: TextView = dialog.findViewById(R.id.id_error_text)
        errorText.visibility = View.GONE

        todoTitle.setText(mTodoList[position].name)
        todoDesc.setText(mTodoList[position].description)
        if (mTodoList[position].priority == null) {
            todoPriority.setSelection(0)
        } else {
            todoPriority.setSelection(mTodoList[position].priority.ordinal + 1)
        }


        val okayBtn = dialog.findViewById<Button>(R.id.id_dialog_btn_okay)
        val cancelBtn = dialog.findViewById<Button>(R.id.id_dialog_btn_cancel)
        okayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_700))
        cancelBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.high_pr))

        okayBtn.setOnClickListener {
            if (todoTitle.text.toString().isEmpty() || todoDesc.text.toString().isEmpty()) {
                errorText.visibility = View.VISIBLE
            } else {
                errorText.visibility = View.GONE
                updateTodo(position,
                    todoTitle.text.toString(),
                    todoDesc.text.toString(),
                    todoPriority.selectedItem.toString())
                dialog.dismiss()
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
    }

    override fun getItemCount(): Int {
        return mTodoList.size
    }

    fun setData(todoList: ArrayList<TakeNote>) {
        mTodoList = todoList
        mTodoList.reverse()
        mFullTodoList.clear()
        mFullTodoList = ArrayList(mTodoList)
        notifyItemRangeChanged(0, mTodoList.size)
    }

    fun getData(): ArrayList<TakeNote> {
        return mTodoList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = constraint.toString()
                val searchTodoList = ArrayList<TakeNote>()

                if (constraint == null || constraint.isEmpty()) {
                    searchTodoList.addAll(mFullTodoList)
                } else {
                    for(todo in mTodoList) {
                        if (todo.name.lowercase(Locale.ROOT).contains(searchString.lowercase(
                                Locale.ROOT))) {
                            searchTodoList.add(todo)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = searchTodoList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mTodoList.clear()
                mTodoList.addAll(results?.values as ArrayList<TakeNote>)
                notifyDataSetChanged()
            }

        }
    }

}