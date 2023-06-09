package com.moblie.programming.assignment.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.moblie.programming.assignment.manager.CertificateManager
import com.moblie.programming.assignment.type.Certificate
import com.moblie.programming.assignment.type.Common
import com.moblie.programming.assignment.ui.component.Header
import com.moblie.programming.assignment.ui.theme.AssignmentTheme
import androidx.compose.runtime.livedata.observeAsState
import com.moblie.programming.assignment.manager.DBHelper

class InfoActivity : ComponentActivity() {
    lateinit var certificate: Certificate
    lateinit var db: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DBHelper(this)
        certificate = (intent.extras?.get("Certificate") ?: Certificate()) as Certificate
        setContent {
            AssignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    DefaultPreview()
                }
            }
        }
    }

    @Composable
    fun InfoBox(title: String, text: String) {
        Surface(
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                if (text.startsWith("http://") || text.startsWith("https://")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
                    startActivity(intent)
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text(text = text, maxLines = 1, fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun MoreInfoBox(text: String) {
        Surface(
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "More Information", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text(text = text, maxLines = 5, fontSize = 12.sp)
            }
        }
    }

    @Composable
    fun InfoImage() {
        val imageUrl = certificate.imageLink.ifBlank { Common.DUMMY_CERTIFICATE.IMAGE }
        Surface(
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Gray)
                        .height(200.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }

    @Composable
    fun bottomButton() {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = Color.DarkGray,
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            ) {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { finish() }
                ) {
                    Text("OK")
                }
            }
        }
    }

    private fun getBookMarkString(isInit: Boolean = false): String {
        var isFav = certificate.isFav
        if (!isInit) {
            isFav = !isFav
            CertificateManager.updateFav(certificate, isFav)
        }
        if (isFav) {
            db.addFav(certificate)
            Toast.makeText(applicationContext, "북마크 추가 완료", Toast.LENGTH_SHORT).show()
        }
        else {
            db.deleteFav(certificate)
            Toast.makeText(applicationContext, "북마크 삭제 완료", Toast.LENGTH_SHORT).show()
        }
        return if (isFav) "★" else "☆"
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 480)
    @Composable
    fun DefaultPreview() {
        var (isFav, setFav) = remember { mutableStateOf(getBookMarkString(true)) }
        Column() {
            Surface(
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
            ) {
                Column() {
                    Header(certificate.name, color = Color.White, { setFav(getBookMarkString()) }, isFav)
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(state = ScrollState(0), enabled = true)
            ) {
                InfoImage()
                InfoBox("organization", certificate.organization)
                certificate.amount.map {
                    InfoBox(it.key,"₩ ${it.amount}")
                }
                InfoBox( "Website",certificate.link)
                MoreInfoBox(certificate.information)
            }
            bottomButton()
        }
    }
}