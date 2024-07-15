package com.example.nfcdemo

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nfcdemo.databinding.ReadNfcTagBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ReadFragment : Fragment(), NfcFragment {

    private lateinit var binding: ReadNfcTagBinding
    private lateinit var nfcAdapter: NfcAdapter
    private var bottomSheetDialog: BottomSheetDialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        hideBottomSheet("No tag detected. Please try again.", false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReadNfcTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        binding.readButton.setOnClickListener {
            showBottomSheet()
            binding.detectionStatusTextView.text = "Scanning for NFC tags..."
            enableNfc()
            handler.postDelayed(timeoutRunnable, 10000) // 10 seconds timeout
        }

        binding.btnWrite.setOnClickListener {
            findNavController().navigate(R.id.action_readFragment_to_write)
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfc()
    }

    override fun onPause() {
        super.onPause()
        disableNfc()
        handler.removeCallbacks(timeoutRunnable) // Remove timeout callback if the fragment is paused
    }

    override fun onNfcIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            binding.tagContentTextView.text = "NFC Tag detected!"
            handler.removeCallbacks(timeoutRunnable) // Remove timeout callback if tag is detected
            hideBottomSheet("Read successful!", true)
        }
    }

    private fun enableNfc() {
        val intent = Intent(context, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val intentFilter = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter.enableForegroundDispatch(requireActivity(), pendingIntent, intentFilter, null)
    }

    private fun disableNfc() {
        nfcAdapter.disableForegroundDispatch(requireActivity())
    }

    private fun showBottomSheet() {
        Log.d(TAG, "Showing bottom sheet...")
        val view = layoutInflater.inflate(R.layout.bottom_sheet_progress, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()

        // Set cancel button listener
        view.findViewById<Button>(R.id.cancelLoaderButton).setOnClickListener {
            handler.removeCallbacks(timeoutRunnable) // Remove timeout callback if cancelled
            hideBottomSheet("Scan canceled", false)
        }
    }

    private fun hideBottomSheet(message: String, isSuccess: Boolean) {
        Log.d(TAG, "Hiding bottom sheet...")
        bottomSheetDialog?.dismiss()
        binding.detectionStatusTextView.text = message
        if (isSuccess) {
            binding.tagContentTextView.text = "✔️ $message"
        } else {
            binding.tagContentTextView.text = "❌ $message"
        }
    }

    companion object {
        private const val TAG = "ReadFragment"
    }
}
