package com.example.cupcakeapp

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cupcakeapp.data.CupcakeScreen
import com.example.cupcakeapp.data.OrderUiState
import com.example.cupcakeapp.ui.FlavorScreen
import com.example.cupcakeapp.ui.GreetingScreen
import com.example.cupcakeapp.ui.HomeScreen
import com.example.cupcakeapp.ui.PickupScreen
import com.example.cupcakeapp.ui.SettingsScreen
import com.example.cupcakeapp.ui.StartOrderScreen
import com.example.cupcakeapp.ui.SummaryScreen
import com.example.cupcakeapp.ui.theme.CupcakeappTheme
import com.example.cupcakeapp.viewmodel.MainViewModel
import com.example.cupcakeapp.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen.title) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(
    mainViewModel: MainViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    
    val currentRoute = backStackEntry?.destination?.route ?: CupcakeScreen.Home.name
    val currentScreen = try {
        if (currentRoute.startsWith(CupcakeScreen.Greeting.name)) {
            CupcakeScreen.Greeting
        } else {
            CupcakeScreen.valueOf(currentRoute)
        }
    } catch (e: Exception) {
        CupcakeScreen.Home
    }

    CupcakeappTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                CupcakeAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        ) { innerPadding ->
            val uiState by orderViewModel.uiState.collectAsState()

            NavHost(
                navController = navController,
                startDestination = CupcakeScreen.Home.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = CupcakeScreen.Home.name) {
                    HomeScreen(
                        onGreetClicked = { name ->
                            navController.navigate("${CupcakeScreen.Greeting.name}/$name")
                        },
                        onSettingsClicked = {
                            navController.navigate(CupcakeScreen.Settings.name)
                        },
                        onStartOrderClicked = {
                            navController.navigate(CupcakeScreen.Start.name)
                        }
                    )
                }
                composable(route = "${CupcakeScreen.Greeting.name}/{name}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    GreetingScreen(
                        name = name,
                        onBackClicked = { navController.popBackStack() }
                    )
                }
                composable(route = CupcakeScreen.Settings.name) {
                    SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeChanged = { mainViewModel.setTheme(it) }
                    )
                }
                composable(route = CupcakeScreen.Start.name) {
                    StartOrderScreen(
                        onNextButtonClicked = {
                            orderViewModel.setQuantity(it)
                            navController.navigate(CupcakeScreen.Flavor.name)
                        }
                    )
                }
                composable(route = CupcakeScreen.Flavor.name) {
                    FlavorScreen(
                        subtotal = uiState.price,
                        onNextButtonClicked = {
                            navController.navigate(CupcakeScreen.Pickup.name)
                        },
                        onCancelButtonClicked = {
                            cancelOrderAndNavigateToStart(orderViewModel, navController)
                        },
                        onSelectionChanged = { orderViewModel.setFlavor(it) }
                    )
                }
                composable(route = CupcakeScreen.Pickup.name) {
                    PickupScreen(
                        subtotal = uiState.price,
                        options = uiState.pickupOptions,
                        onNextButtonClicked = {
                            navController.navigate(CupcakeScreen.Summary.name)
                        },
                        onCancelButtonClicked = {
                            cancelOrderAndNavigateToStart(orderViewModel, navController)
                        },
                        onSelectionChanged = { orderViewModel.setDate(it) }
                    )
                }
                composable(route = CupcakeScreen.Summary.name) {
                    val context = LocalContext.current
                    SummaryScreen(
                        orderUiState = uiState,
                        onSendButtonClicked = { subject, summary ->
                            shareOrder(context, subject = subject, summary = summary)
                        },
                        onCancelButtonClicked = {
                            cancelOrderAndNavigateToStart(orderViewModel, navController)
                        }
                    )
                }
            }
        }
    }
}

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}

private fun shareOrder(context: Context, subject: String, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            "Order Summary"
        )
    )
}
