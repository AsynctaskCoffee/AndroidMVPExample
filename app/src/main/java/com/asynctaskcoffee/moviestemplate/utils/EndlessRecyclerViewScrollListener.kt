package com.asynctaskcoffee.moviestemplate.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class EndlessRecyclerViewScrollListener(
    layoutManager: GridLayoutManager,
    onNextPageListener: OnNextPageListener
) : RecyclerView.OnScrollListener() {

    private var visibleThreshold = 5
    private var currentPage = 1
    private var previousTotalItemCount = 0
    private var loading = true
    private var startingPageIndex = 1
    private var itemPerRequest = 20
    private var onNextPageListener: OnNextPageListener? = onNextPageListener

    private var mLayoutManager: GridLayoutManager = layoutManager


    private fun getLastVisibleItem(
        lastVisibleItemPositions: IntArray
    ): Int {
        var maxSize = 0
        for (i in 0..lastVisibleItemPositions.size) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager.itemCount
        lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && (lastVisibleItemPosition + visibleThreshold * mLayoutManager.spanCount) > totalItemCount && (itemPerRequest * currentPage == totalItemCount)) {
            currentPage++
            if (onNextPageListener != null)
                onNextPageListener!!.nextPageRequest(currentPage, totalItemCount, view)
            loading = true
        }
    }

    fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    interface OnNextPageListener {
        fun nextPageRequest(page: Int, totalItemsCount: Int, view: RecyclerView)
    }

}