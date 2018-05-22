package com.jason.carfinder.fragments

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import com.jason.carfinder.models.DIST_ASC
import com.jason.carfinder.models.getSortTitles


class SortDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var listener: OnSortSelectedListener
    private lateinit var selected: String

    interface OnSortSelectedListener {
        fun onSortSelected(sort: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnSortSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${this.javaClass.name} must implement OnSortSelectedListener")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("curSelected", selected)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selected = savedInstanceState?.getString("curSelected") ?: arguments.getString(SELECTED_SORT)

        val builder = AlertDialog.Builder(activity)
        builder.setSingleChoiceItems(
                ArrayAdapter(
                        activity,
                        android.R.layout.simple_list_item_single_choice,
                        android.R.id.text1,
                        getSortTitles()
                ),
                getSortTitles().indexOf(arguments.getSerializable(SELECTED_SORT)),
                this
        )
        builder.setPositiveButton("Apply", { _, _ ->
            listener.onSortSelected(selected)
        })
        builder.setNegativeButton("Clear", { _, _ ->
            listener.onSortSelected(DIST_ASC)
        })
        builder.setTitle("Sort")

        return builder.create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        selected = getSortTitles()[which]
    }

    companion object {
        val TAG: String = SortDialogFragment::class.java.name
        const val SELECTED_SORT = "selected-sort"

        fun newInstance(selectedSort: String): SortDialogFragment {
            val fragment = SortDialogFragment()
            val args = Bundle()
            args.putString(SELECTED_SORT, selectedSort)
            fragment.arguments = args
            return fragment
        }
    }

}