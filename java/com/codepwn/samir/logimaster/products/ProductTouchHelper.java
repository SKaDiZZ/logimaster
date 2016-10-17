package com.codepwn.samir.logimaster.products;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.codepwn.samir.logimaster.R;

/**
 * Manage recycler view touches swipe and other cool stuff
 */
public class ProductTouchHelper extends ItemTouchHelper.SimpleCallback {

    Context context;
    Activity activity;
    RecyclerView recyclerView;

    Drawable background;
    Drawable xMark;
    int xMarkMargin;
    boolean initiated;

    public ProductTouchHelper(int dragDirs, int swipeDirs, Context context, RecyclerView recyclerView) {
        super(dragDirs, swipeDirs);

        this.context = context;
        this.activity = (Activity) context;
        this.recyclerView = recyclerView;

    }

    private void init() {
        background = new ColorDrawable(Color.RED);
        xMark = ContextCompat.getDrawable(activity, R.drawable.ic_clear_24dp);
        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        xMarkMargin = (int) activity.getResources().getDimension(R.dimen.ic_clear_margin);
        initiated = true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        ProductRecycleAdapter productAdapter = (ProductRecycleAdapter) recyclerView.getAdapter();
        if (productAdapter.isUndoOn() && productAdapter.isPendingRemoval(position)) {
            return 0;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int swipedPosition = viewHolder.getAdapterPosition();
        ProductRecycleAdapter productAdapter = (ProductRecycleAdapter) recyclerView.getAdapter();
        boolean undoOn = productAdapter.isUndoOn();
        if (undoOn) {
            productAdapter.pendingRemoval(swipedPosition);
        } else {
            productAdapter.remove(swipedPosition);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        // draw red background
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        // draw x mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = xMark.getIntrinsicWidth();
        int intrinsicHeight = xMark.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;
        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

        xMark.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
