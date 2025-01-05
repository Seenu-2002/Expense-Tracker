package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.CategoryDefaults
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AddCategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    navController: NavController,
    viewModel: AddCategoryViewModel = hiltViewModel()
) {

    var selectedIcon: Int? by remember {
        mutableStateOf(null)
    }
    var title: String by remember {
        mutableStateOf("")
    }
    var iconBackgroundColor: Color by remember {
        mutableStateOf(CategoryDefaults.categoryColors.random())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Create a New Category") // TODO: String res
                }, navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable(
                                onClick = {
                                    navController.popBackStack()
                                }
                            )
                            .padding(8.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconItem(
                        modifier = Modifier
                            .size(52.dp)
                            .padding(4.dp),
                        res = selectedIcon,
                        iconBackground = iconBackgroundColor
                    )
                    OutlinedTextField(modifier = Modifier
                        .padding(8.dp)
                        .weight(1F), value = title, onValueChange = {
                        title = it
                    }, placeholder = {
                        Text("Category Name")// TODO: String res
                    })
                }

                Column(modifier = Modifier.weight(1F)) {
                    Text(text = "Icons")// TODO: String res
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        columns = GridCells.Adaptive(minSize = 48.dp)
                    ) {
                        items(CategoryDefaults.categoryIconsList.size) {
                            CategoryIconItem(
                                modifier = Modifier
                                    .size(52.dp)
                                    .padding(4.dp),
                                res = CategoryDefaults.categoryIconsList[it]
                            ) { res ->
                                selectedIcon = res
                            }
                        }
                    }
                    Text(text = "Colors")// TODO: String res
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        columns = GridCells.Adaptive(minSize = 48.dp)
                    ) {
                        items(CategoryDefaults.categoryColors.size) {
                            val color = CategoryDefaults.categoryColors[it]
                            CategoryColorItem(
                                modifier = Modifier
                                    .size(52.dp)
                                    .padding(4.dp),
                                color = color,
                                isSelected = color == iconBackgroundColor
                            ) { color ->
                                iconBackgroundColor = color
                            }
                        }
                    }
                }
            }

            Button(modifier = Modifier.padding(16.dp), enabled = title.isNotBlank(), onClick = {

            }) {
                Text("Create")// TODO: Strings.xml
            }
        }
    }

}

@Preview
@Composable
fun CategoryIconItem(
    modifier: Modifier = Modifier,
    @DrawableRes res: Int? = R.drawable.bolt,
    iconBackground: Color = Color(0xFF3F7BAA),
    onClicked: ((Int?) -> Unit)? = null
) {
    val isBackgroundDark = iconBackground.luminance() < 0.5f
    val iconColor = if (isBackgroundDark) Color.White else Color.Black
    Box(
        modifier = modifier
            .background(iconBackground, CircleShape)
            .clip(CircleShape)
            .clickable(enabled = onClicked != null) {
                onClicked?.invoke(res)
            },
        contentAlignment = Alignment.Center
    ) {
        res?.let {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(res),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = iconColor)
            )
        }
    }
}

@Preview
@Composable
fun CategoryColorItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = true,
    color: Color = Color(0xFF3F7BAA),
    onClicked: ((Color) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .background(color, CircleShape)
            .clip(CircleShape)
            .clickable(enabled = onClicked != null, indication = null, interactionSource = null) {
                onClicked?.invoke(color)
            },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Image(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color(0x33000000))
                    .padding(8.dp),
                painter = painterResource(R.drawable.check),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.White)
            )
        }
    }
}

