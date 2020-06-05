/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.summer.demo.ui.fragment.self

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView

import com.summer.demo.R

/**
 * Sample that shows tinting of Drawables programmatically and of Drawable resources in XML.
 * Tinting is set on a nine-patch drawable through the "tint" and "tintMode" parameters.
 * A color state list is referenced as the tint color, which  defines colors for different
 * states of a View (for example disabled/enabled, focused, pressed or selected).
 * Programmatically, tinting is applied to a Drawable through its "setColorFilter" method, with
 * a reference to a color and a PorterDuff blend mode. The color and blend mode can be
 * changed from the UI.
 *
 * @see android.graphics.drawable.Drawable.setColorFilter
 */
class DrawableTintingFragment : Fragment() {

    /**
     * Image that tinting is applied to programmatically.
     */
    private var mImage: ImageView? = null

    /**
     * Seekbar for alpha component of tinting color.
     */
    private var mAlphaBar: SeekBar? = null
    /**
     * Seekbar for red component of tinting color.
     */
    private var mRedBar: SeekBar? = null
    /**
     * Seekbar for green bar of tinting color.
     */
    private var mGreenBar: SeekBar? = null
    /**
     * Seekbar for blue bar of tinting color.
     */
    private var mBlueBar: SeekBar? = null

    /**
     * Text label for alpha component seekbar.
     */
    private var mAlphaText: TextView? = null
    /**
     * Text label for red component seekbar.
     */
    private var mRedText: TextView? = null
    /**
     * Text label for green component seekbar.
     */
    private var mGreenText: TextView? = null
    /**
     * Text label for blue component seekbar.
     */
    private var mBlueText: TextView? = null

    /**
     * Selector for blend type for color tinting.
     */
    private var mBlendSpinner: Spinner? = null

    /**
     * Computed color for tinting of drawable.
     */
    private var mHintColor: Int = 0

    /**
     * Selected color tinting mode.
     */
    private var mMode: PorterDuff.Mode? = null

    /**
     * Computes the [Color] value from selection on ARGB sliders.
     *
     * @return color computed from selected ARGB values
     */
    val color: Int
        get() {
            val alpha = mAlphaBar!!.progress
            val red = mRedBar!!.progress
            val green = mGreenBar!!.progress
            val blue = mBlueBar!!.progress

            return Color.argb(alpha, red, green, blue)
        }

    /**
     * Returns the [android.graphics.PorterDuff.Mode] for the selected tint mode option.
     *
     * @return selected tint mode
     */
    val tintMode: PorterDuff.Mode
        get() = MODES[mBlendSpinner!!.selectedItemPosition]

    /**
     * Listener that updates the tint when a blend mode is selected.
     */
    private val mBlendListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
            // Selected a blend mode and update the tint of image
            updateTint(color, tintMode)
        }

        override fun onNothingSelected(adapterView: AdapterView<*>) {

        }

    }

    /**
     * Seekbar listener that updates the tinted color when the progress bar has changed.
     */
    private val mSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            // Update the tinted color from all selections in the UI
            updateTint(color, tintMode)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tinting, null)

        // Set a drawable as the image to display
        mImage = v.findViewById<View>(R.id.image) as ImageView
        mImage!!.setImageResource(R.drawable.ic_launcher)

        // Get text labels and seekbars for the four color components: ARGB
        mAlphaBar = v.findViewById<View>(R.id.alphaSeek) as SeekBar
        mAlphaText = v.findViewById<View>(R.id.alphaText) as TextView
        mGreenBar = v.findViewById<View>(R.id.greenSeek) as SeekBar
        mGreenText = v.findViewById<View>(R.id.greenText) as TextView
        mRedBar = v.findViewById<View>(R.id.redSeek) as SeekBar
        mRedText = v.findViewById<View>(R.id.redText) as TextView
        mBlueText = v.findViewById<View>(R.id.blueText) as TextView
        mBlueBar = v.findViewById<View>(R.id.blueSeek) as SeekBar

        // Set a listener to update tinted image when selections have changed
        mAlphaBar!!.setOnSeekBarChangeListener(mSeekBarListener)
        mRedBar!!.setOnSeekBarChangeListener(mSeekBarListener)
        mGreenBar!!.setOnSeekBarChangeListener(mSeekBarListener)
        mBlueBar!!.setOnSeekBarChangeListener(mSeekBarListener)
        v.findViewById<View>(R.id.button).setOnClickListener { }

        // Set up the spinner for blend mode selection from a string array resource
        mBlendSpinner = v.findViewById<View>(R.id.blendSpinner) as Spinner
        val sa = ArrayAdapter.createFromResource(activity!!,
                R.array.blend_modes, android.R.layout.simple_spinner_dropdown_item)
        mBlendSpinner!!.adapter = sa
        // Set a listener to update the tinted image when a blend mode is selected
        mBlendSpinner!!.onItemSelectedListener = mBlendListener
        // Select the first item
        mBlendSpinner!!.setSelection(0)
        mMode = MODES[0]

        if (savedInstanceState != null) {
            // Restore the previous state if this fragment has been restored
            mBlendSpinner!!.setSelection(savedInstanceState.getInt(STATE_BLEND))
            mAlphaBar!!.progress = savedInstanceState.getInt(STATE_ALPHA)
            mRedBar!!.progress = savedInstanceState.getInt(STATE_RED)
            mGreenBar!!.progress = savedInstanceState.getInt(STATE_GREEN)
            mBlueBar!!.progress = savedInstanceState.getInt(STATE_BLUE)
        }

        // Apply the default blend mode and color
        updateTint(color, tintMode)

        return v
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "state saved.")
        outState.putInt(STATE_BLEND, mBlendSpinner!!.selectedItemPosition)
        outState.putInt(STATE_ALPHA, mAlphaBar!!.progress)
        outState.putInt(STATE_RED, mRedBar!!.progress)
        outState.putInt(STATE_GREEN, mGreenBar!!.progress)
        outState.putInt(STATE_BLUE, mBlueBar!!.progress)
    }

    /**
     * Update the tint of the image with the color set in the seekbars and selected blend mode.
     * The seekbars are set to a maximum of 255, with one for each of the four components of the
     * ARGB color. (Alpha, Red, Green, Blue.) Once a color has been computed using
     * [Color.argb], it is set togethe with the blend mode on the background
     * image using
     * [android.widget.ImageView.setColorFilter].
     */
    fun updateTint(color: Int, mode: PorterDuff.Mode) {
        // Set the color hint of the image: ARGB
        mHintColor = color

        // Set the color tint mode based on the selection of the Spinner
        mMode = mode

        // Log selection
        Log.d(TAG, String.format("Updating tint with color [ARGB: %d,%d,%d,%d] and mode [%s]",
                Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color),
                mode.toString()))

        // Apply the color tint for the selected tint mode
        mImage!!.setColorFilter(mHintColor, mMode)

        // Update the text for each label with the value of each channel
        mAlphaText!!.text = getString(R.string.value_alpha, Color.alpha(color))
        mRedText!!.text = getString(R.string.value_red, Color.red(color))
        mGreenText!!.text = getString(R.string.value_green, Color.green(color))
        mBlueText!!.text = getString(R.string.value_blue, Color.blue(color))
    }

    companion object {

        /**
         * String that identifies logging output from this Fragment.
         */
        private val TAG = "DrawableTintingFragment"

        /**
         * Identifier for state of blend mod spinner in state bundle.
         */
        private val STATE_BLEND = "DRAWABLETINTING_BLEND"
        /**
         * Identifier for state of alpha seek bar in state bundle.
         */
        private val STATE_ALPHA = "DRAWABLETINTING_ALPHA"
        /**
         * Identifier for state of red seek bar in state bundle.
         */
        private val STATE_RED = "DRAWABLETINTING_RED"
        /**
         * Identifier for state of green seek bar in state bundle.
         */
        private val STATE_GREEN = "DRAWABLETINTING_GREEN"
        /**
         * Identifier for state of blue seek bar in state bundle.
         */
        private val STATE_BLUE = "DRAWABLETINTING_BLUE"

        /**
         * Available tinting modes. Note that this array must be kept in sync with the
         * `blend_modes` string array that provides labels for these modes.
         */
        private val MODES = arrayOf(PorterDuff.Mode.ADD, PorterDuff.Mode.CLEAR, PorterDuff.Mode.DARKEN, PorterDuff.Mode.DST, PorterDuff.Mode.DST_ATOP, PorterDuff.Mode.DST_IN, PorterDuff.Mode.DST_OUT, PorterDuff.Mode.DST_OVER, PorterDuff.Mode.LIGHTEN, PorterDuff.Mode.MULTIPLY, PorterDuff.Mode.OVERLAY, PorterDuff.Mode.SCREEN, PorterDuff.Mode.SRC, PorterDuff.Mode.SRC_ATOP, PorterDuff.Mode.SRC_IN, PorterDuff.Mode.SRC_OUT, PorterDuff.Mode.SRC_OVER, PorterDuff.Mode.XOR)
    }
}
