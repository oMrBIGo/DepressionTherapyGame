package com.depressiontherapygame.Users.GameTetris.listeners;

/**
 * interface for item click in adapter list
 *
 * @param <T> model for item in list
 */
public interface ItemClickListener<T> {
    void onItemClicked(int position, T model);
}