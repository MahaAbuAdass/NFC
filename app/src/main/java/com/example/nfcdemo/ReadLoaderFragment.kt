package com.example.nfcdemo

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nfcdemo.databinding.LoaderReadFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ReadLoaderFragment : Fragment(), NfcFragment {

    private lateinit var binding: LoaderReadFragmentBinding
    private var nfcAdapter: NfcAdapter? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timeoutMillis: Long = 10000 // 10 seconds timeout
    private var isTagDetected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoaderReadFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        if (nfcAdapter == null) {
            Toast.makeText(requireContext(), "NFC is not available on this device", Toast.LENGTH_SHORT).show()
            navigateBack()
            return
        }

        showBottomSheet()
        enableNfc()
        handler.postDelayed(timeoutRunnable, timeoutMillis) // Start timeout countdown
    }

    override fun onResume() {
        super.onResume()
        enableNfc()
    }

    override fun onPause() {
        super.onPause()
        disableNfc()
        handler.removeCallbacksAndMessages(null) // Remove all callbacks and messages
    }

    override fun onNfcIntent(intent: Intent) {
        Log.d(TAG, "onNfcIntent")

        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            isTagDetected = true
            Toast.makeText(requireContext(), "NFC Tag detected!", Toast.LENGTH_SHORT).show()

            handler.removeCallbacks(timeoutRunnable) // Stop timeout countdown
            hideBottomSheet(true)

            // Handle NDEF messages
            handleNdefTag(tag)
        }
    }

    private fun handleNdefTag(tag: Tag?) {
        Log.d(TAG, "handle tag")

        tag ?: return

        val ndef = Ndef.get(tag)
        ndef?.let {
            try {
                it.connect()
                val message = it.ndefMessage
                Log.d(TAG, "NDEF Message: ${message?.toString()}")
                // Process the NDEF message further as needed
            } catch (e: Exception) {
                Log.e(TAG, "Error reading NFC tag", e)
                Toast.makeText(requireContext(), "Error reading NFC tag", Toast.LENGTH_SHORT).show()
            } finally {
                it.close()
            }
        } ?: run {
            Log.e(TAG, "NDEF is null - Tag is not NDEF formatted or NDEF is not supported.")
            Toast.makeText(requireContext(), "Tag is not NDEF formatted or NDEF is not supported.", Toast.LENGTH_SHORT).show()
        }
    }

    private val timeoutRunnable = Runnable {
        if (!isTagDetected) {
            hideBottomSheet(false)
        }
    }

    private fun enableNfc() {
        Log.d(TAG, "enable nfc Message")

        val intent = Intent(requireContext(), requireActivity().javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE  // Use FLAG_IMMUTABLE for Android S and above
        )
        val intentFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val techList = arrayOf(arrayOf(Ndef::class.java.name)) // Adjust as per your supported tech list
        val intentFiltersArray = arrayOf(intentFilter)
        nfcAdapter?.enableForegroundDispatch(requireActivity(), pendingIntent, intentFiltersArray, techList)
    }

    private fun disableNfc() {
        Log.d(TAG, "disable nfc Message")

        nfcAdapter?.disableForegroundDispatch(requireActivity())
    }

    private fun showBottomSheet() {
        Log.d(TAG, "Showing bottom sheet...")
        val view = layoutInflater.inflate(R.layout.bottom_sheet_progress, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()

        // Set cancel button listener
        view.findViewById<Button>(R.id.cancelLoaderButton).setOnClickListener {
            handler.removeCallbacks(timeoutRunnable) // Stop timeout countdown
            hideBottomSheet(false)
            requireActivity().onBackPressed()
        }
    }

    private fun hideBottomSheet(isSuccess: Boolean) {
        Log.d(TAG, "Hiding bottom sheet...")
        bottomSheetDialog?.dismiss()

        if (isSuccess) {
            Toast.makeText(requireContext(), "Read successful!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Read failed. Please try again.", Toast.LENGTH_SHORT).show()
        }

        requireActivity().onBackPressed()
    }

    private fun navigateBack() {
        if (findNavController().currentDestination?.id == R.id.readFragment) {
            findNavController().popBackStack()
        }
    }

    companion object {
        private const val TAG = "ReadLoaderFragment"
    }
}
