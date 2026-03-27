package com.example.test01

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, MainFragment())
                .commit()
        }
    }
}

class MainFragment : Fragment() {
    private var player: MediaPlayer? = null
    private var turtleImage: ImageButton? = null
    private var turtleName: TextView? = null
    private var currentNameResId: Int = R.string.leo_full

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = MediaPlayer.create(requireContext(), R.raw.tmnt_theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val turtleGroup: RadioGroup = view.findViewById(R.id.turtle_group)
        turtleImage = view.findViewById(R.id.turtle_image)
        turtleName = view.findViewById(R.id.turtle_name)

        turtleGroup.setOnCheckedChangeListener { _, checkedId ->
            pickTurtle(checkedId)
        }

        turtleImage?.setOnClickListener {
            turtleName?.setText(currentNameResId)
        }

        turtleGroup.check(R.id.radio_leo)
        pickTurtle(R.id.radio_leo)
        return view
    }

    override fun onResume() {
        super.onResume()
        player?.isLooping = true
        player?.start()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
        player?.seekTo(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    private fun pickTurtle(checkedId: Int) {
        when (checkedId) {
            R.id.radio_leo -> updateTurtle(R.drawable.tmntleo, R.string.leo_full)
            R.id.radio_don -> updateTurtle(R.drawable.tmntdon, R.string.don_full)
            R.id.radio_raph -> updateTurtle(R.drawable.tmntraph, R.string.raph_full)
            R.id.radio_mike -> updateTurtle(R.drawable.tmntmike, R.string.mike_full)
        }
    }

    private fun updateTurtle(imageResId: Int, nameResId: Int) {
        currentNameResId = nameResId
        turtleImage?.setImageResource(imageResId)
        turtleName?.setText(R.string.tap_hint)
    }
}
