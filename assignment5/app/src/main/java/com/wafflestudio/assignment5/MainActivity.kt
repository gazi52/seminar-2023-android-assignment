package com.wafflestudio.assignment5

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.contextaware.withContextAvailable
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MyApp(Modifier.fillMaxSize())
            }
        }
    }
    @Composable
    private fun MyApp(
        modifier: Modifier = Modifier,
        navController: NavHostController = rememberNavController(),
        startDestination: String = "Onboarding",
    )
    {
        NavHost(navController = navController, startDestination = startDestination){
            composable("Onboarding"){
                OnboardingScreen(
                    onNavigateToTutorial = {navController.navigate("Tutorial")},
                    onNavigateToSearch = {navController.navigate("Search")},
                    onNavigateToCLock = {navController.navigate("Clock")}
                )
            }
            composable("Tutorial"){ TutorialScreen()}
            composable("Search"){ SearchScreen(
                onSearchClicked = { text ->
                    navController.navigate("Movie/$text")
                }
            )}
            composable("Movie/{text}"){
                val text = it.arguments?.getString("text") ?: ""
                MovieScreen(text) }

            composable("Clock"){ ClockScreen() }
        }
    }
    @Composable
    fun OnboardingScreen(
        modifier: Modifier = Modifier,
        onNavigateToTutorial : () -> Unit,
        onNavigateToSearch : () -> Unit,
        onNavigateToCLock : () -> Unit,) {

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 12.dp),
                onClick = onNavigateToTutorial
            ) {
                Text("튜토리얼")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 12.dp),
                onClick = onNavigateToSearch
            ) {
                Text("영화 검색")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 12.dp),
                onClick = onNavigateToCLock
            ) {
                Text("디지털 시계")
            }
        }

    }
    @Composable
    private fun TutorialScreen(
        modifier: Modifier = Modifier,
        names: List<String> = List(1000) { "$it" }
    ) {
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            items(items = names){ name ->
                Greeting(name = name)
            }
        }
    }
    @Composable
    private fun Greeting(name: String){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ){
            CardContent(name)
        }
    }
    @Composable
    private fun CardContent(name: String) {
        var expanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = "Hello, ")
                Text(
                    text = name, style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                if (expanded) {
                    Text(
                        text = ("Composem ipsum color sit lazy, " +
                                "padding theme elit, sed do bouncy. ").repeat(4),
                    )
                }
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )
            }
        }
    }
    @Composable
    fun SearchScreen(
        modifier: Modifier = Modifier,
        onSearchClicked : (String) -> Unit,
    ){
        var text by rememberSaveable {
            mutableStateOf("")
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newText -> text = newText},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(20.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSearchClicked(text)
                    }
                )
            )
        }

    }
    @Composable
    fun MovieScreen(
        text : String,
        //viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
        viewModel: MainViewModel = viewModel(factory = MyViewModelFactory()),
        modifier: Modifier = Modifier,
        ){

        /*val scope = rememberCoroutineScope()
        LaunchedEffect(Unit){
            scope.launch(Dispatchers.IO){
                val movieList = viewModel.api.getMovie()

            }
        }*/
        Text(text = text)
    }

    @Composable
    fun ClockScreen(
        modifier: Modifier = Modifier,
    ){

    }

    @Preview(showBackground = true, widthDp = 320)
    @Composable
    fun TutorialPreview(){
        MyApplicationTheme {
            TutorialScreen()
        }
    }
    @Preview(showBackground = true, widthDp = 320)
    @Composable
    fun SearchPreview(){
        MyApplicationTheme {
            SearchScreen(onSearchClicked = {})
        }
    }
    @Preview(showBackground = true, widthDp = 320)
    @Composable
    fun ClockPreview(){
        MyApplicationTheme {
            ClockScreen()
        }
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 320)
    @Composable
    fun OnboardingPreview() {
        MyApplicationTheme {
            //OnboardingScreen()
        }
    }
    @Preview
    @Composable
    fun MyAppPreview() {
        MyApplicationTheme {
            MyApp(Modifier.fillMaxSize())
        }
    }
}