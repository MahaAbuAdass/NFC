package com.example.nfcdemo

import android.content.Intent

interface NfcFragment {
    fun onNfcIntent(intent: Intent)
}