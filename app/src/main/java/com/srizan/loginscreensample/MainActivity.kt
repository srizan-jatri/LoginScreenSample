package com.srizan.loginscreensample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.srizan.loginscreensample.ui.theme.LoginScreenSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreenSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {
    val stateHolder = rememberLoginScreenStateHolder()

    /**
     * Handle Events from view model
     * */
    LaunchedEffect(true) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.ShowToast -> Toast.makeText(
                    stateHolder.context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
                is LoginEvent.ShowSnackBar -> {
                    //TODO Show SnackBar
                }
            }
        }
    }

    when (viewModel.loginState.value) {
        LoginScreenSate.Error -> ErrorLayout(onRetryClick = { viewModel.restoreIdleState() })
        LoginScreenSate.Idle -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo"
                )
                OutlinedTextField(value = viewModel.email.value,
                    label = { Text(text = "Email Address") },
                    supportingText = { if (viewModel.emailSupportText.isNotEmpty()) Text(text = viewModel.emailSupportText) },
                    singleLine = true,
                    onValueChange = {
                        viewModel.updateUserName(it)
                    }
                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                OutlinedTextField(value = viewModel.password.value,
                    label = { Text(text = "Password") },
                    supportingText = {
                        if (viewModel.passwordSupportText.isNotEmpty()) Text(text = viewModel.passwordSupportText)
                    },
                    singleLine = true,
                    trailingIcon = {
                        val image = if (stateHolder.passwordIsVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        IconButton(onClick = {
                            stateHolder.passwordIsVisible = !stateHolder.passwordIsVisible
                        }
                        ) {
                            Icon(imageVector = image, contentDescription = "Eye Button")
                        }
                    },
                    visualTransformation = if (stateHolder.passwordIsVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    onValueChange = { viewModel.updatePassword(it) }
                )
                Spacer(modifier = Modifier.padding(top = 30.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                    onClick = { viewModel.login() },
                ) {
                    Text(text = "Login")
                }
            }
        }
        LoginScreenSate.Loading -> LoadingScreen()
        LoginScreenSate.Success -> HomeScreen(
            email = viewModel.email.value,
            password = viewModel.password.value
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorLayout(modifier: Modifier = Modifier, onRetryClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Something went wrong, please try again...")
        Button(onClick = onRetryClick) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, email: String, password: String) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome Home\nYour email is: $email\nand \nPassword is: $password")
    }
}

/**
 * A plane State Holder Only for Ui Elements State
 * */
class LoginScreenStateHolder(val context: Context) {
    var passwordIsVisible by mutableStateOf(false)
}

@Composable
fun rememberLoginScreenStateHolder(
    context: Context = LocalContext.current
): LoginScreenStateHolder = remember { LoginScreenStateHolder(context) }


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginScreenSampleTheme {
        LoginScreen()
    }
}