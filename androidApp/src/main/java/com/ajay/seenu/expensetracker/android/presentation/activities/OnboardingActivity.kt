package com.ajay.seenu.expensetracker.android.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ajay.seenu.expensetracker.android.presentation.theme.ExpenseTrackerTheme
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.OnBoardingViewModel
import com.ajay.seenu.expensetracker.domain.model.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachObserver()
        viewModel.init()
        setContent {
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            val isDarkThemeEnabled = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM_THEME -> isSystemInDarkTheme()
            }
            ExpenseTrackerTheme(darkTheme = isDarkThemeEnabled) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (text, signBtn) = createRefs()
                    Text(
                        stringResource(R.string.onboarding_text),
                        modifier = Modifier.constrainAs(text) {
                            centerVerticallyTo(parent)
                            centerHorizontallyTo(parent)
                        },
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .constrainAs(signBtn) {
                            bottom.linkTo(parent.bottom, 16.dp)
                            centerHorizontallyTo(parent)
                        },
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            viewModel.completeOnboarding()
                        }) {
                        Text(stringResource(R.string.onboarding_get_started))
                    }
                }
            }
        }
    }

    private fun attachObserver() {
        lifecycleScope.launch {
            viewModel.onUserLoggedIn.collectLatest {
                when {
                    it -> {
                        if (!isDestroyed) {
                            startActivity(
                                Intent(
                                    this@OnboardingActivity,
                                    MainActivity::class.java
                                ).also { intent ->
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                            finish()
                        }
                    }
                }
            }
        }
    }

}