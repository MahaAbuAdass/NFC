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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nfcdemo.databinding.LoaderWriteFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class WriteLoader : Fragment(), NfcFragment {

    private lateinit var binding: LoaderWriteFragmentBinding
    private lateinit var nfcAdapter: NfcAdapter
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var nfcMessage: String? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoaderWriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        // Show bottom sheet with loader when fragment is created
        showBottomSheet()

        // Enable NFC
        enableNfc()
    }

    override fun onResume() {
        super.onResume()
        Log.v("NFC TAG"  , "onResume executed")

        enableNfc()
    }

    override fun onPause() {
        super.onPause()
        Log.v("NFC TAG"  , "onPause executed")

        disableNfc()

    }

    override fun onNfcIntent(intent: Intent) {
        Log.v("NFC TAG"  , "onNfcIntent executed")
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            // Read NFC tag data here
            val ndef = Ndef.get(tag)
            nfcMessage = ndef?.let {
                it.connect()
                val message = it.ndefMessage
                it.close()
                message?.records?.getOrNull(0)?.let { record ->
                    String(record.payload, charset("UTF-8"))
                }
            }
            handler.post {
                if (nfcMessage != null) {
                    // NFC Tag detected successfully
                    hideBottomSheet("NFC Tag detected: $nfcMessage", true)
                } else {
                    // NFC Tag read failed
                    hideBottomSheet("NFC Tag read failed.", false)
                }
            }
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
            PendingIntent.FLAG_IMMUTABLE
        )
        val intentFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val intentFiltersArray = arrayOf(intentFilter)
        nfcAdapter.enableForegroundDispatch(requireActivity(), pendingIntent, intentFiltersArray, null)
    }


    private fun disableNfc() {
        Log.v("NFC TAG"  , "disableNfc")

        nfcAdapter.disableForegroundDispatch(requireActivity())
    }

    private fun showBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_progress, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()

        view.findViewById<View>(R.id.cancelLoaderButton)?.setOnClickListener {
            bottomSheetDialog?.dismiss()
            requireActivity().onBackPressed()
        }
    }

    private fun hideBottomSheet(message: String, isSuccess: Boolean) {
        // Dismiss the bottom sheet first
        bottomSheetDialog?.dismiss()

        // Display toast message based on success or failure
        if (isSuccess) {
            // Display a success message
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } else {
            // Display a failure message
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Navigate back to the previous screen
        requireActivity().onBackPressed()
    }
}
