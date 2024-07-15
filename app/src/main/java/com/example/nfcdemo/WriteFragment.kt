package com.example.nfcdemo

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nfcdemo.databinding.WriteNfcTagBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class WriteFragment : Fragment() {
    private lateinit var binding: WriteNfcTagBinding
    private lateinit var nfcAdapter: NfcAdapter
    private var nfcMessage: NdefMessage? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WriteNfcTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        binding.writeButton.setOnClickListener {
            val text = binding.editText.text.toString()
            if (text.isNotEmpty()) {
                findNavController().navigate(R.id.action_write_to_loader_write)
//                showBottomSheet()
//                val record = NdefRecord.createTextRecord("en", text)
//                nfcMessage = NdefMessage(arrayOf(record))
//                binding.detectionStatusTextView.text = "Hold NFC tag near device..."
//                // Start NFC writing
//                enableNfc()
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        enableNfc()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        disableNfc()
//    }

//    override fun onNfcIntent(intent: Intent) {
//        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
//            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
//            // Write NFC tag data here
//            nfcMessage?.let {
//                val ndef = Ndef.get(tag)
//                ndef.connect()
//                ndef.writeNdefMessage(it)
//                ndef.close()
//                binding.tagContentTextView.text = "NFC Tag written successfully!"
//                binding.detectionStatusTextView.text = ""
//                hideBottomSheet("Write successful!")
//            }
//        }
//    }

//    private fun enableNfc() {
//        val intent = Intent(context, javaClass).apply {
//            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE  // Changed flag to FLAG_IMMUTABLE
//        )
//        val intentFilter = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
//        nfcAdapter.enableForegroundDispatch(requireActivity(), pendingIntent, intentFilter, null)
//    }
//
//    private fun disableNfc() {
//        nfcAdapter.disableForegroundDispatch(requireActivity())
//    }
//
//    private fun showBottomSheet() {
//        val view = layoutInflater.inflate(R.layout.bottom_sheet_progress, null)
//        bottomSheetDialog = BottomSheetDialog(requireContext())
//        bottomSheetDialog?.setContentView(view)
//        bottomSheetDialog?.show()
//    }
//
//    private fun hideBottomSheet(message: String) {
//        bottomSheetDialog?.dismiss()
//        binding.detectionStatusTextView.text = message
//    }
}
