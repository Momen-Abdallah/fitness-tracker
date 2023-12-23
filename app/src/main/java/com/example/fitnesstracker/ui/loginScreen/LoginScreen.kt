package com.example.fitnesstracker.ui.loginScreen

import android.app.Activity
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.LoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class LoginScreen : Fragment() {


    private lateinit var binding: LoginScreenBinding
    private val viewModel: LoginScreenViewModel by viewModels()

    //    @Inject
//    lateinit var signInClient : SignInClient
//    @Inject
//    lateinit var signInRequest: BeginSignInRequest
//    @Inject
//    lateinit var auth : FirebaseAuth
//    @Inject
//    lateinit var firestore : FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = LoginScreenBinding.inflate(inflater, container, false)

        if (requireContext().getSharedPreferences("pref", MODE_PRIVATE).getBoolean("login", false))
            findNavController().navigate(R.id.action_loginScreen_to_homeScreen)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /* val launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
             if (it.resultCode == Activity.RESULT_OK){




                 val credential = signInClient.getSignInCredentialFromIntent(it.data)
 //                Toast.makeText(requireContext(), "hhh", Toast.LENGTH_SHORT).show()
 //                val idToken = credential.googleIdToken
 //                val username = credential.id
 //                val password = credential.password

                 try {

                     auth.signInWithCredential(GoogleAuthProvider.getCredential(credential.googleIdToken, null)).addOnSuccessListener {
                         firestore.collection("users").document(auth.uid!!).set(mapOf(
                             "name" to credential.givenName,
                             "profile_picture" to credential.profilePictureUri


                         )).addOnSuccessListener {
                             Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                         }
                             .addOnFailureListener {
                                 Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                             }
                     }.addOnFailureListener {
                         Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                     }
                 }catch (e : Exception){
                     Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                 }


 //                auth.signInWithCustomToken(credential.googleIdToken!!).addOnSuccessListener {
 //                    firestore.collection("users").document(auth.uid!!).set(mapOf(
 //                        "name" to credential.givenName
 //                    )).addOnSuccessListener {
 //                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
 //                    }
 //                        .addOnFailureListener {
 //                            Toast.makeText(requireContext(), "Failed firestore", Toast.LENGTH_SHORT).show()
 //
 //                        }
 //                }
 //                    .addOnFailureListener {
 //                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
 //
 //                    }
             }
         }


         binding.googleSignInButton.setOnClickListener {


             Identity.getSignInClient(requireContext()).beginSignIn(
                 BeginSignInRequest.builder()
                     .setGoogleIdTokenRequestOptions(
                         BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                             .setSupported(true)
                             .setServerClientId(getString(R.string.web_client_id))
                             .setFilterByAuthorizedAccounts(false)
                             .build()
                     )
                     .setAutoSelectEnabled(true)
                     .build()
             ).addOnSuccessListener {
 //                startIntentSenderForResult(it.pendingIntent.intentSender,111,
 //                    null,0,0,0,null)
                 launcher.launch(IntentSenderRequest.Builder(it.pendingIntent.intentSender).build())

             }.addOnFailureListener {
                 Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
             }


 //            signInClient.beginSignIn(signInRequest).addOnSuccessListener {
 //
 //                launcher.launch(IntentSenderRequest.Builder(it.pendingIntent.intentSender).build())
 //
 //                startIntentSenderForResult(it.pendingIntent.intentSender,111,
 //                    null,0,0,0,null)
 //
 //            }.addOnFailureListener {
 //                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
 //            }
         }*/


//        val fitnessOptions = FitnessOptions.builder().addDataType(DataType.TYPE_STEP_COUNT_DELTA)
//        Fitness.getRecordingClient(requireContext(), GoogleSignIn.getAccountForExtension(requireContext(),FitnessOptions.builder().build()))

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val credential = viewModel.onTapClient.getSignInCredentialFromIntent(it.data)


                    GlobalScope.launch(Dispatchers.IO) {
                        viewModel.signInWithCredential(credential)
                        viewModel.addUserToFirestore(credential)

                        val editor =
                            requireContext().getSharedPreferences("pref", MODE_PRIVATE).edit()
                        editor.putBoolean("login", true)
                        editor.apply()

//                        withContext(Dispatchers.Main) {
//                            findNavController().navigate(R.id.action_loginScreen_to_homeScreen)
//                        }
                    }

                    val fitnessOptions = FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .build()

                    val googleSignInAccount =
                        GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions)
//        Fitness.getRecordingClient(requireContext(),googleSignInAccount)


                    if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
                        GoogleSignIn.requestPermissions(
                            this@LoginScreen, // Activity
                            1,
                            googleSignInAccount,
                            fitnessOptions
                        )
                    }
                    Fitness.getRecordingClient(
                        requireContext(),
                        GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions)
                    )
                        .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                        .addOnSuccessListener {
                            Log.i(ContentValues.TAG, "Subscription was successful!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "There was a problem subscribing ", e)
                        }
                    findNavController().navigate(R.id.action_loginScreen_to_homeScreen)


                }
            }

        binding.googleSignInButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val result = viewModel.signInWithGoogle()

                if (result != null) {
                    launcher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "error with signIn with google",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }


    }


}