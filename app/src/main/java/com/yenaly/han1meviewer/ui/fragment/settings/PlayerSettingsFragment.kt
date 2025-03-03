package com.yenaly.han1meviewer.ui.fragment.settings

import android.os.Bundle
import androidx.annotation.IntRange
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.yenaly.han1meviewer.R
import com.yenaly.han1meviewer.ui.activity.SettingsActivity
import com.yenaly.han1meviewer.ui.fragment.IToolbarFragment
import com.yenaly.han1meviewer.ui.view.HJzvdStd
import com.yenaly.yenaly_libs.base.settings.YenalySettingsFragment
import rikka.preference.SimpleMenuPreference

/**
 * @project Han1meViewer
 * @author Yenaly Liew
 * @time 2023/09/04 004 16:28
 */
class PlayerSettingsFragment : YenalySettingsFragment(R.xml.settings_player),
    IToolbarFragment<SettingsActivity> {

    companion object {
        const val SHOW_BOTTOM_PROGRESS = "show_bottom_progress"
        const val PLAYER_SPEED = "player_speed"
        const val SLIDE_SENSITIVITY = "slide_sensitivity"
        const val LONG_PRESS_SPEED_TIMES = "long_press_speed_times"
    }

    private val showBottomProgressPref
            by safePreference<SwitchPreferenceCompat>(SHOW_BOTTOM_PROGRESS)
    private val playerSpeed
            by safePreference<SimpleMenuPreference>(PLAYER_SPEED)
    private val slideSensitivity
            by safePreference<SeekBarPreference>(SLIDE_SENSITIVITY)
    private val longPressSpeedTimesPref
            by safePreference<SimpleMenuPreference>(LONG_PRESS_SPEED_TIMES)

    override fun onStart() {
        super.onStart()
        (activity as SettingsActivity).setupToolbar()
    }

    override fun onPreferencesCreated(savedInstanceState: Bundle?) {
        playerSpeed.apply {
            entries = HJzvdStd.speedStringArray
            entryValues = Array(HJzvdStd.speedArray.size) {
                HJzvdStd.speedArray[it].toString()
            }
            // 不能直接用 defaultValue 设置，没效果
            if (value == null) setValueIndex(HJzvdStd.DEF_SPEED_INDEX)
        }
        slideSensitivity.apply {
            setDefaultValue(HJzvdStd.DEF_PROGRESS_SLIDE_SENSITIVITY)
            summary = getString(
                R.string.current_slide_sensitivity,
                value.toPrettySensitivityString()
            )
            setOnPreferenceChangeListener { pref, newVal ->
                pref.summary = pref.context.getString(
                    R.string.current_slide_sensitivity,
                    (newVal as Int).toPrettySensitivityString()
                )
                return@setOnPreferenceChangeListener true
            }
        }
        longPressSpeedTimesPref.apply {
            entries = arrayOf(
                "1倍", "1.5倍", "2倍", "2.5倍（預設）", "2.8倍",
                "3倍", "3.2倍", "3.5倍", "3.8倍", "4倍"
            )
            entryValues = arrayOf(
                "1", "1.5", "2", "2.5", "2.8",
                "3", "3.2", "3.5", "3.8", "4"
            )
            if (value == null) setValueIndex(3)
        }
    }

    /**
     * 將數字靈敏度轉換為漂亮的字串
     */
    private fun @receiver:IntRange(from = 1, to = 9) Int.toPrettySensitivityString(): String {
        return when (this) {
            1, 2 -> "高"
            3, 4 -> "較高"
            5 -> "適中"
            6 -> "稍低"
            7 -> "較低"
            8 -> "低"
            9 -> "極低"
            else -> throw IllegalStateException("Invalid sensitivity value: $this")
        }
    }

    override fun SettingsActivity.setupToolbar() {
        supportActionBar!!.setTitle(R.string.player_settings)
    }
}