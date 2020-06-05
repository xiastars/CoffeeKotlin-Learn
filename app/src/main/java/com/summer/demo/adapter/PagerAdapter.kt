package com.summer.demo.adapter

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import android.database.DataSetObservable
import android.database.DataSetObserver
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup

/**
 * Base class providing the adapter to populate pages inside of
 * a [VerticalViewPager].  You will most likely want to use a more
 * specific implementation of this, such as
 * [android.support.v4.app.FragmentPagerAdapter] or
 * [android.support.v4.app.FragmentStatePagerAdapter].
 *
 *
 * When you implement a PagerAdapter, you must override the following methods
 * at minimum:
 *
 *  * [.instantiateItem]
 *  * [.destroyItem]
 *  * [.getCount]
 *  * [.isViewFromObject]
 *
 *
 *
 * PagerAdapter is more general than the adapters used for
 * [AdapterViews][android.widget.AdapterView]. Instead of providing a
 * View recycling mechanism directly ViewPager uses callbacks to indicate the
 * steps taken during an update. A PagerAdapter may implement a form of View
 * recycling if desired or use a more sophisticated method of managing page
 * Views such as Fragment transactions where each page is represented by its
 * own Fragment.
 *
 *
 * ViewPager associates each page with a key Object instead of working with
 * Views directly. This key is used to track and uniquely identify a given page
 * independent of its position in the adapter. A call to the PagerAdapter method
 * [.startUpdate] indicates that the contents of the ViewPager
 * are about to change. One or more calls to [.instantiateItem]
 * and/or [.destroyItem] will follow, and the end
 * of an update will be signaled by a call to [.finishUpdate].
 * By the time [finishUpdate][.finishUpdate] returns the views
 * associated with the key objects returned by
 * [instantiateItem][.instantiateItem] should be added to
 * the parent ViewGroup passed to these methods and the views associated with
 * the keys passed to [destroyItem][.destroyItem]
 * should be removed. The method [.isViewFromObject] identifies
 * whether a page View is associated with a given key object.
 *
 *
 * A very simple PagerAdapter may choose to use the page Views themselves
 * as key objects, returning them from [.instantiateItem]
 * after creation and adding them to the parent ViewGroup. A matching
 * [.destroyItem] implementation would remove the
 * View from the parent ViewGroup and [.isViewFromObject]
 * could be implemented as `return view == object;`.
 *
 *
 * PagerAdapter supports data set changes. Data set changes must occur on the
 * main thread and must end with a call to [.notifyDataSetChanged] similar
 * to AdapterView adapters derived from [android.widget.BaseAdapter]. A data
 * set change may involve pages being added, removed, or changing position. The
 * ViewPager will keep the current page active provided the adapter implements
 * the method [.getItemPosition].
 */
abstract class PagerAdapter {
    private val mObservable = DataSetObservable()

    /**
     * Return the number of views available.
     */
    abstract val count: Int

    /**
     * Called when a change in the shown pages is going to start being made.
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
    fun startUpdate(container: ViewGroup) {
        startUpdate(container as View)
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * [.finishUpdate].
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    fun instantiateItem(container: ViewGroup, position: Int): Any {
        return instantiateItem(container as View, position)
    }

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from [.finishUpdate].
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed.
     * @param object The same object that was returned by
     * [.instantiateItem].
     */
    fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        destroyItem(container as View, position, `object`)
    }

    /**
     * Called to inform the adapter of which item is currently considered to
     * be the "primary", that is the one show to the user as the current page.
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position that is now the primary.
     * @param object The same object that was returned by
     * [.instantiateItem].
     */
    fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        setPrimaryItem(container as View, position, `object`)
    }

    /**
     * Called when the a change in the shown pages has been completed.  At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
    fun finishUpdate(container: ViewGroup) {
        finishUpdate(container as View)
    }

    /**
     * Called when a change in the shown pages is going to start being made.
     * @param container The containing View which is displaying this adapter's
     * page views.
     *
     */
    @Deprecated("Use {@link #startUpdate(ViewGroup)}")
    fun startUpdate(container: View) {
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * [.finishUpdate].
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     *
     */
    @Deprecated("Use {@link #instantiateItem(ViewGroup, int)}")
    fun instantiateItem(container: View, position: Int): Any {
        throw UnsupportedOperationException(
                "Required method instantiateItem was not overridden")
    }

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from [.finishUpdate].
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed.
     * @param object The same object that was returned by
     * [.instantiateItem].
     *
     */
    @Deprecated("Use {@link #destroyItem(ViewGroup, int, Object)}")
    fun destroyItem(container: View, position: Int, `object`: Any) {
        throw UnsupportedOperationException("Required method destroyItem was not overridden")
    }

    /**
     * Called to inform the adapter of which item is currently considered to
     * be the "primary", that is the one show to the user as the current page.
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position that is now the primary.
     * @param object The same object that was returned by
     * [.instantiateItem].
     *
     */
    @Deprecated("Use {@link #setPrimaryItem(ViewGroup, int, Object)}")
    fun setPrimaryItem(container: View, position: Int, `object`: Any) {
    }

    /**
     * Called when the a change in the shown pages has been completed.  At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
     * @param container The containing View which is displaying this adapter's
     * page views.
     *
     */
    @Deprecated("Use {@link #finishUpdate(ViewGroup)}")
    fun finishUpdate(container: View) {
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by [.instantiateItem]. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view Page View to check for association with `object`
     * @param object Object to check for association with `view`
     * @return true if `view` is associated with the key object `object`
     */
    abstract fun isViewFromObject(view: View, `object`: Any): Boolean

    /**
     * Save any instance state associated with this adapter and its pages that should be
     * restored if the current UI state needs to be reconstructed.
     *
     * @return Saved state for this adapter
     */
    fun saveState(): Parcelable? {
        return null
    }

    /**
     * Restore any instance state associated with this adapter and its pages
     * that was previously saved by [.saveState].
     *
     * @param state State previously saved by a call to [.saveState]
     * @param loader A ClassLoader that should be used to instantiate any restored objects
     */
    fun restoreState(state: Parcelable, loader: ClassLoader) {}

    /**
     * Called when the host view is attempting to determine if an item's position
     * has changed. Returns [.POSITION_UNCHANGED] if the position of the given
     * item has not changed or [.POSITION_NONE] if the item is no longer present
     * in the adapter.
     *
     *
     * The default implementation assumes that items will never
     * change position and always returns [.POSITION_UNCHANGED].
     *
     * @param object Object representing an item, previously returned by a call to
     * [.instantiateItem].
     * @return object's new position index from [0, [.getCount]),
     * [.POSITION_UNCHANGED] if the object's position has not changed,
     * or [.POSITION_NONE] if the item is no longer present.
     */
    fun getItemPosition(`object`: Any): Int {
        return POSITION_UNCHANGED
    }

    /**
     * This method should be called by the application if the data backing this adapter has changed
     * and associated views should update.
     */
    fun notifyDataSetChanged() {
        mObservable.notifyChanged()
    }

    internal fun registerDataSetObserver(observer: DataSetObserver) {
        mObservable.registerObserver(observer)
    }

    internal fun unregisterDataSetObserver(observer: DataSetObserver) {
        mObservable.unregisterObserver(observer)
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    fun getPageTitle(position: Int): CharSequence? {
        return null
    }

    /**
     * Returns the proportional width of a given page as a percentage of the
     * ViewPager's measured width from (0.f-1.f]
     *
     * @param position The position of the page requested
     * @return Proportional width for the given page position
     */
    fun getPageWidth(position: Int): Float {
        return 1f
    }

    fun getPageHeight(position: Int): Float {
        return 1f
    }

    companion object {

        val POSITION_UNCHANGED = -1
        val POSITION_NONE = -2
    }
}
