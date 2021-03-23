import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.KeyStroke
import androidx.compose.ui.window.Menu
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.gavarent.EmailApplication
import ru.gavarent.GavarentTheme
import ru.gavarent.saveContent
import java.awt.FileDialog
import java.awt.FileDialog.LOAD
import java.awt.FileDialog.SAVE
import java.io.File
import kotlin.system.exitProcess

enum class JobStates {
   PREPARATION,
   IN_PROGRESS,
   FINISHED
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
   val emailApplication = EmailApplication()

   Window(
      title = "Email Validation Tool",
      size = IntSize(700, 500),
      /*icon = ,*/
      menuBar = getMenuBar(),
      onDismissRequest = {
         println("User close app")
      }
   ) {
      val currentWindow = AppWindowAmbient.current!!
      val jobState = remember { mutableStateOf(JobStates.PREPARATION) }
      emailApplication.guiFields.onChange = { value ->
         println("Зашли $value")
         if (value) {
            jobState.value = JobStates.FINISHED
         }
      }

      GavarentTheme {
         val snackBar = remember { mutableStateOf("") }
         Column(
            modifier = Modifier.fillMaxSize()
         ) {
            Row(modifier = Modifier.height(400.dp)) {
               Column(
                  modifier = Modifier
                     .width(450.dp)
                     .height(400.dp)
                     .padding(start = 10.dp, end = 10.dp)
               ) {

                  val realEmail = remember { mutableStateOf(TextFieldValue()) }
                  val ehlo = remember { mutableStateOf(TextFieldValue()) }
                  val checkList = remember { mutableStateOf(TextFieldValue()) }
                  val blackList = remember { mutableStateOf(TextFieldValue()) }
                  val whiteList = remember { mutableStateOf(TextFieldValue()) }

                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "Real email (*)",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = realEmail.value,
                        onValueChange = {
                           realEmail.value = it
                           emailApplication.guiFields.realEmail = it.text
                        },
                        singleLine = true,
                     )
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "HELO/EHLO answer (*)",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = ehlo.value,
                        onValueChange = {
                           ehlo.value = it
                           emailApplication.guiFields.ehlo = it.text
                        },
                        singleLine = true,
                     )
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "Check List (*)",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = checkList.value,
                        onValueChange = {
                           //checkList.value = it
                        },
                        singleLine = true,
                     )
                     Spacer(Modifier.width(8.dp).height(8.dp))
                     Button(
                        onClick = {
                           FileDialog(currentWindow.window, "Файл с проверяемыми адресами").apply {
                              this.isVisible = true
                              val file: String? = this.file
                              file?.let {
                                 emailApplication.guiFields.checkList = File(this.directory, it)
                                 checkList.value = TextFieldValue(it)
                              }
                           }
                        },
                        enabled = jobState.value == JobStates.PREPARATION,
                        modifier = Modifier.width(24.dp).height(24.dp)
                     ) {
                        Text("...")
                     }
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "Black List",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = blackList.value,
                        onValueChange = {
                        //   blackList.value = it
                        },
                        singleLine = true,
                        modifier = Modifier
                     )
                     Spacer(Modifier.width(8.dp).height(8.dp))
                     Button(
                        onClick = {
                           FileDialog(currentWindow.window, "Файл с черным списком").apply {
                              this.isVisible = true
                              val file: String? = this.file
                              file?.let {
                                 emailApplication.guiFields.blackList = File(this.directory, it)
                                 blackList.value = TextFieldValue(it)
                              }
                           }
                        },
                        enabled = jobState.value == JobStates.PREPARATION,
                        modifier = Modifier.width(24.dp).height(24.dp)
                     ) {
                        Text("...")
                     }
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                  ) {
                     Text(
                        "White List",
                        modifier = Modifier.width(100.dp)
                     )
                     TextField(
                        value = whiteList.value,
                        onValueChange = {
                           //whiteList.value = it
                        },
                        singleLine = true,
                        modifier = Modifier
                     )
                     Spacer(Modifier.width(8.dp).height(8.dp))
                     Button(
                        onClick = {
                           FileDialog(currentWindow.window, "Файл с белыми списками", LOAD).apply {
                              this.isVisible = true
                              val file: String? = this.file
                              file?.let {
                                 emailApplication.guiFields.whiteList = File(this.directory, it)
                                 whiteList.value = TextFieldValue(it)
                              }

                           }
                        },
                        enabled = jobState.value == JobStates.PREPARATION,
                        modifier = Modifier.width(24.dp).height(24.dp)
                     ) {
                        Text("...")
                     }
                  }
                  Spacer(Modifier.width(8.dp).height(8.dp))
                  Row {

                     val progress = remember { mutableStateOf(0.0f) }
                     if (jobState.value == JobStates.PREPARATION) {


                        Button(onClick = {
                           snackBar.value = ""
                           if (emailApplication.guiFields.checkList != null &&
                              emailApplication.guiFields.checkList!!.exists() &&
                              emailApplication.guiFields.realEmail.isNotEmpty() &&
                              emailApplication.guiFields.ehlo.isNotEmpty()
                           ) {
                              jobState.value = JobStates.IN_PROGRESS
                              GlobalScope.launch(Dispatchers.IO) {
                                 emailApplication.process()
                              }
                           } else {
                              snackBar.value = "Заполнены не все необходимые поля"
                           }
                        }) {
                           Text(text = "Начать")
                        }

                     }
                     if (jobState.value == JobStates.IN_PROGRESS) {
                        if (progress.value > 0.0) {
                           LinearProgressIndicator(
                              progress = progress.value,
                              modifier = Modifier.fillMaxWidth()
                           )
                        }
                     }

                     if (jobState.value == JobStates.FINISHED) {
                        Row {
                           Button(
                              onClick = {
                                 FileDialog(currentWindow.window, "Файл с хорошими адресами", SAVE).apply {
                                    this.isVisible = true
                                    val file: String? = this.file
                                    file?.let {
                                       GlobalScope.launch(Dispatchers.IO) {
                                          File(
                                             this@apply.directory,
                                             it
                                          ).saveContent(emailApplication.guiFields.goodEmails)
                                       }
                                    }
                                 }
                              },
                              modifier = Modifier.width(200.dp)
                           ) {
                              Text(
                                 text = "Сохранить хорошие адресса",
                                 textAlign = TextAlign.Center
                              )
                           }
                           Spacer(Modifier.width(10.dp).height(10.dp))
                           Button(
                              onClick = {
                                 FileDialog(currentWindow.window, "Файл с плохими адресами", SAVE).apply {
                                    this.isVisible = true
                                    val file: String? = this.file
                                    file?.let {
                                       GlobalScope.launch(Dispatchers.IO) {
                                          File(
                                             this@apply.directory,
                                             it
                                          ).saveContent(emailApplication.guiFields.goodEmails)
                                       }

                                    }
                                 }
                              },
                              modifier = Modifier.width(200.dp)
                           )
                           {
                              Text(
                                 text = "Сохранить плохие адресса",
                                 textAlign = TextAlign.Center
                              )
                           }
                        }
                     }
                  }
               }
               Column(
                  modifier = Modifier
                     .width(250.dp)
                     .height(400.dp)
                     .background(Color.Green)
               ) { }
            }
            Row(
               modifier = Modifier.fillMaxSize(),
               verticalAlignment = Alignment.Bottom
            ) {
               if (snackBar.value.isNotEmpty()) {
                  Snackbar {
                     Text(text = snackBar.value)
                  }
               }
            }

         }

      }
   }
}

fun getMenuBar(): MenuBar {
   val fileMenu = Menu(
      name = "File",
      item = arrayOf(
         MenuItem(
            name = "Exit",
            onClick = { exitProcess(0) },
            shortcut = KeyStroke(Key.X)
         )
      )
   )
   val helpMenu = Menu(
      name = "Help",
      item = arrayOf(
         MenuItem(
            name = "Web Site",
            onClick = {}
         )
      )
   )
   return MenuBar(fileMenu, helpMenu)
}