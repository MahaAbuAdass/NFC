package com.example.nfcdemo

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nfcdemo.databinding.LoaderReadFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.IOException

class ReadLoaderFragment : Fragment(), NfcFragment {

    private lateinit var binding: LoaderReadFragmentBinding
    private var nfcAdapter: NfcAdapter? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timeoutMillis: Long = 10000 // 10 seconds timeout
    private var isTagDetected = false

    companion object {
        private const val TAG = "NFC TAG"
    }

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
        Log.v(TAG, "onNfcIntent")

        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            isTagDetected = true
            Toast.makeText(requireContext(), "NFC Tag detected!", Toast.LENGTH_SHORT).show()

            handler.removeCallbacks(timeoutRunnable) // Stop timeout countdown
            hideBottomSheet(true)

            // Handle the tag
            handleTag(tag)
        }
    }

    private fun handleTag(tag: Tag?) {
        tag ?: return

        val techList = tag.techList ?: return  // Ensure techList is not null

        for (tech in techList) {
            when (tech) {
                Ndef::class.java.name -> handleNdefTag(tag)
                NfcA::class.java.name -> handleNfcATag(tag)
                NfcB::class.java.name -> handleNfcBTag(tag)
                NfcF::class.java.name -> handleNfcFTag(tag)
                NfcV::class.java.name -> handleNfcVTag(tag)
                IsoDep::class.java.name -> handleIsoDepTag(tag)
                MifareClassic::class.java.name -> handleMifareClassicTag(tag)
                MifareUltralight::class.java.name -> handleMifareUltralightTag(tag)
                else -> handleUnknownTag(tag, tech)
            }
        }
    }

    private fun handleNdefTag(tag: Tag) {
        Log.v(TAG, "handleNdefTag")

        val ndef = Ndef.get(tag)
        ndef?.let {
            try {
                it.connect()
                val message = it.ndefMessage
                Log.v(TAG, "NDEF Message: ${message?.toString()}")
                Toast.makeText(requireContext(), "NDEF Tag detected", Toast.LENGTH_SHORT).show()

                // Process the NDEF message further as needed
            } catch (e: Exception) {
                Log.v(TAG, "Error reading NFC tag", e)
                Toast.makeText(requireContext(), "Error reading NFC tag", Toast.LENGTH_SHORT).show()
            } finally {
                try {
                    it.close()
                } catch (e: IOException) {
                    Log.v(TAG, "Error closing Ndef", e)
                }
            }
        } ?: run {
            Log.v(TAG, "NDEF is null - Tag is not NDEF formatted or NDEF is not supported.")
            Toast.makeText(requireContext(), "Tag is not NDEF formatted or NDEF is not supported.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNfcATag(tag: Tag) {
        Log.v(TAG, "handleNfcATag")
        // Implement handling of NFC-A tags
        Toast.makeText(requireContext(), "NFC-A Tag detected", Toast.LENGTH_SHORT).show()

    }

    private fun handleNfcBTag(tag: Tag) {
        Log.v(TAG, "handleNfcBTag")
        // Implement handling of NFC-B tags
        Toast.makeText(requireContext(), "NFC-B Tag detected", Toast.LENGTH_SHORT).show()

    }

    private fun handleNfcFTag(tag: Tag) {
        Log.v(TAG, "handleNfcFTag")
        // Implement handling of NFC-F tags
        Toast.makeText(requireContext(), "NFC-F Tag detected", Toast.LENGTH_SHORT).show()

    }

    private fun handleNfcVTag(tag: Tag) {
        Log.v(TAG, "handleNfcVTag")
        // Implement handling of NFC-V tags
        Toast.makeText(requireContext(), "NFC-V Tag detected", Toast.LENGTH_SHORT).show()

    }

    private fun handleIsoDepTag(tag: Tag) {
        Log.v(TAG, "handleIsoDepTag")
        // Implement handling of IsoDep tags
        Toast.makeText(requireContext(), "IsoDep Tag detected", Toast.LENGTH_SHORT).show()

    }

    private fun handleMifareClassicTag(tag: Tag) {
        Log.v(TAG, "handleMifareClassicTag")
        // Implement handling of MifareClassic tags
        Toast.makeText(requireContext(), "Mifare Classic Tag detected", Toast.LENGTH_SHORT).show()

    }

    private fun handleMifareUltralightTag(tag: Tag) {
        Log.v(TAG, "handleMifareUltralightTag")
        // Implement handling of MifareUltralight tags
        Toast.makeText(requireContext(), "Mifare Ultralight Tag detected", Toast.LENGTH_SHORT).show()

    }

    private fun handleUnknownTag(tag: Tag, technology: String) {
        Log.v(TAG, "handleUnknownTag: $technology")
        Toast.makeText(requireContext(), "Unknown tag technology: $technology", Toast.LENGTH_SHORT).show()
    }

    private val timeoutRunnable = Runnable {
        if (!isTagDetected) {
            hideBottomSheet(false)
        }
    }

    private fun enableNfc() {
        val intent = Intent(requireContext(), requireActivity().javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val filter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)

        val intentFiltersArray = arrayOf(filter)
        val techListsArray = arrayOf(
            arrayOf(Ndef::class.java.name),
            arrayOf(NfcA::class.java.name),
            arrayOf(NfcB::class.java.name),
            arrayOf(NfcF::class.java.name),
            arrayOf(NfcV::class.java.name),
            arrayOf(IsoDep::class.java.name),
            arrayOf(MifareClassic::class.java.name),
            arrayOf(MifareUltralight::class.java.name)
        )

        nfcAdapter?.enableForegroundDispatch(
            requireActivity(),
            pendingIntent,
            intentFiltersArray,
            techListsArray
        )
    }

    private fun disableNfc() {
        nfcAdapter?.disableForegroundDispatch(requireActivity())
    }

    private fun showBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(R.layout.bottom_sheet_progress)
        bottomSheetDialog?.setCancelable(false)
        bottomSheetDialog?.show()
    }

    private fun hideBottomSheet(success: Boolean) {
        bottomSheetDialog?.dismiss()
        if (!success) {
            Toast.makeText(requireContext(), "NFC tag reading timed out", Toast.LENGTH_SHORT).show()
            navigateBack()
        }
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }
}
