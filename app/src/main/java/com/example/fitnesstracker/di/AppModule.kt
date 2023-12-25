package com.example.fitnesstracker.di

import android.content.Context
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.repo.AuthRepository
import com.example.fitnesstracker.data.repo.StepsRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun  provideGoogleSignInAccount(@ApplicationContext context: Context) : GoogleSignInAccount {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_MOVE_MINUTES,FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
            .build()

        return  GoogleSignIn.getAccountForExtension(context,fitnessOptions)

    }
    @Provides
    @Singleton
    fun provideStepsRepository(@ApplicationContext context: Context, googleSignInAccount: GoogleSignInAccount) = StepsRepository(context,googleSignInAccount)
    @Provides
    @Singleton
    fun providesAuth() = Firebase.auth
    @Provides
    @Singleton
    fun providesFireStore() = Firebase.firestore
//    @Provides
//    @Singleton
//    fun providesContext(@ApplicationContext context: ApplicationContext) = context

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        fireStore : FirebaseFirestore,
        auth : FirebaseAuth,
        signInRequest: BeginSignInRequest,
        signInClient: SignInClient
    ) = AuthRepository(context ,fireStore,auth,signInRequest,signInClient)

    @Singleton
    @Provides
    fun provideSignInRequest(
        @ApplicationContext context: Context
    ) =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
             )
            .setAutoSelectEnabled(true)
            .build()

    @Singleton
    @Provides
    fun provideOnTapClient(@ApplicationContext context: Context)=
        Identity.getSignInClient(context)





}