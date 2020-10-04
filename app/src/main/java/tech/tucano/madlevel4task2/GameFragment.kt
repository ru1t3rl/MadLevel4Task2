package tech.tucano.madlevel4task2

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.tucano.madlevel4task2.databinding.FragmentGameBinding
import java.text.DateFormat.getDateTimeInstance
import kotlin.random.Random


/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding

    private lateinit var gameRepository: GameRepository
    private val mainScope = CoroutineScope(Dispatchers.Main)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameRepository = GameRepository(requireContext())

        setupSets()

        getGamesFromDatabase()
    }

    private fun setupSets() {
        binding.btnPaper.setOnClickListener {
            play(getString(R.string.paper))
        }

        binding.btnRock.setOnClickListener {
            play(getString(R.string.rock))
        }

        binding.btnScissors.setOnClickListener {
            play(getString(R.string.scissors))
        }
    }

    private fun getGamesFromDatabase() {
        mainScope.launch {
            val games = withContext(Dispatchers.IO) {
                gameRepository.getAllGames()
            }

            var win = 0
            var lose = 0
            var draw = 0

            for (i: Game in games) {
                when (i.result) {
                    getString(R.string.win) -> {
                        win++
                    }
                    getString(R.string.lose) -> {
                        lose++
                    }
                    getString(R.string.draw) -> {
                        draw++
                    }
                }
            }

            binding.tvStats.text = "Win:$win Draw:$draw Lose:$lose"
        }
    }

    private fun play(playerSet: String) {
        val cpuSet = resources.getStringArray(R.array.actions)[Random.nextInt(
            0,
            resources.getStringArray(R.array.actions).size
        )]

        // Set the images of both player to their corresponding action
        binding.ivComputer.setImageDrawable(getActionDrawable(cpuSet))
        binding.ivComputer2.setImageDrawable(getActionDrawable(playerSet))

        // Decide the winner
        when (playerSet) {
            getString(R.string.rock) -> {
                when (cpuSet) {
                    getString(R.string.rock) -> binding.tvWinner.text =
                        getString(R.string.result_draw)
                    getString(R.string.paper) -> binding.tvWinner.text =
                        getString(R.string.result_computer_wins)
                    getString(R.string.scissors) -> binding.tvWinner.text =
                        getString(R.string.result_you_win)
                }
            }
            getString(R.string.paper) -> {
                when (cpuSet) {
                    getString(R.string.paper) -> binding.tvWinner.text =
                        getString(R.string.result_draw)
                    getString(R.string.scissors) -> binding.tvWinner.text =
                        getString(R.string.result_computer_wins)
                    getString(R.string.rock) -> binding.tvWinner.text =
                        getString(R.string.result_you_win)
                }
            }
            getString(R.string.scissors) -> {
                when (cpuSet) {
                    getString(R.string.scissors) -> binding.tvWinner.text =
                        getString(R.string.result_draw)
                    getString(R.string.rock) -> binding.tvWinner.text =
                        getString(R.string.result_computer_wins)
                    getString(R.string.paper) -> binding.tvWinner.text =
                        getString(R.string.result_you_win)
                }
            }
        }

        // Insert data in database
        mainScope.launch {
            withContext(Dispatchers.IO) {
                val text = binding.tvWinner.text.toString().toLowerCase()
                val result: String

                if (text.contains(getString(R.string.you).toLowerCase())) {
                    result = getString(R.string.win)
                } else if (text.contains(getString(R.string.computer).toLowerCase())) {
                    result = getString(R.string.lose)
                } else {
                    result = getString(R.string.draw)
                }

                Log.i("GameResult", text + " | Result:" + result)

                // Get the date and time
                val simpleDateFormat = getDateTimeInstance().toString()

                gameRepository.insertGame(Game(simpleDateFormat, playerSet, cpuSet, result))

                // Update the stats
                getGamesFromDatabase()
            }
        }
    }

    private fun getActionDrawable(action: String): Drawable? {
        return when (action.toLowerCase()) {
            getString(R.string.rock) -> ContextCompat.getDrawable(
                requireContext(),
                R.drawable.rock
            )
            getString(R.string.paper) -> ContextCompat.getDrawable(
                requireContext(),
                R.drawable.paper
            )
            getString(R.string.scissors) -> ContextCompat.getDrawable(
                requireContext(),
                R.drawable.scissors
            )
            else -> null
        }
    }
}