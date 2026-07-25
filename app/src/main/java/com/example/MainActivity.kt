package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.components.HomeLinkBottomNav
import com.example.ui.components.HomeLinkTopBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.ClientRegistrationScreen
import com.example.ui.screens.DashboardSearchScreen
import com.example.ui.screens.InAppWebViewerScreen
import com.example.ui.screens.LinkDirectoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SupportChatScreen
import com.example.ui.screens.TechnicianLogsScreen
import com.example.ui.theme.HomeLinkNetworkTheme
import com.example.ui.viewmodel.AppNavSection
import com.example.ui.viewmodel.HomeLinkViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeLinkNetworkTheme {
                val viewModel: HomeLinkViewModel = viewModel(
                    factory = HomeLinkViewModel.Factory(application)
                )

                HomeLinkAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HomeLinkAppContent(viewModel: HomeLinkViewModel) {
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val urlInput by viewModel.urlSearchInput.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingUrl.collectAsStateWithLifecycle()
    val diagnosticResult by viewModel.diagnosticResult.collectAsStateWithLifecycle()
    val selectedWebUrl by viewModel.selectedWebUrl.collectAsStateWithLifecycle()
    val networkLinks by viewModel.networkLinks.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val technicianLogs by viewModel.technicianLogs.collectAsStateWithLifecycle()
    val registeredClients by viewModel.registeredClients.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    if (userRole == UserRole.NONE || currentSection == AppNavSection.LOGIN) {
        LoginScreen(
            loginError = loginError,
            onLoginSubmitted = { password ->
                viewModel.loginWithPassword(password)
            }
        )
    } else {
        Scaffold(
            topBar = {
                HomeLinkTopBar(
                    userRole = userRole,
                    onLogoutClick = { viewModel.logout() }
                )
            },
            bottomBar = {
                if (currentSection != AppNavSection.WEB_VIEWER) {
                    HomeLinkBottomNav(
                        currentSection = currentSection,
                        userRole = userRole,
                        onSectionSelected = { section ->
                            viewModel.setNavSection(section)
                        }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentSection) {
                    AppNavSection.DASHBOARD_SEARCH -> {
                        DashboardSearchScreen(
                            urlInput = urlInput,
                            isAnalyzing = isAnalyzing,
                            diagnosticResult = diagnosticResult,
                            searchHistory = searchHistory,
                            onUrlInputChange = { viewModel.setUrlSearchInput(it) },
                            onPerformSearch = { viewModel.performUrlDiagnostics(it) },
                            onOpenInBrowser = { url -> viewModel.openInInAppBrowser(url) },
                            onClearHistory = { viewModel.clearSearchHistory() }
                        )
                    }

                    AppNavSection.CLIENT_REGISTRATION -> {
                        ClientRegistrationScreen(
                            clients = registeredClients,
                            onRegisterClient = { idNum, name, phone, email, site, plan, status, notes ->
                                viewModel.registerClient(idNum, name, phone, email, site, plan, status, notes)
                            },
                            onUpdateClient = { updatedClient ->
                                viewModel.updateClient(updatedClient)
                            },
                            onDeleteClient = { clientToDelete ->
                                viewModel.deleteClient(clientToDelete)
                            },
                            onTestClientDiagnostics = { siteAddress ->
                                viewModel.setUrlSearchInput(siteAddress)
                                viewModel.performUrlDiagnostics(siteAddress)
                                viewModel.setNavSection(AppNavSection.DASHBOARD_SEARCH)
                            }
                        )
                    }

                    AppNavSection.SUPPORT_CHAT -> {
                        SupportChatScreen(
                            chatMessages = chatMessages,
                            clients = registeredClients,
                            onSendMessage = { text, relId, relName, reqType ->
                                viewModel.sendChatMessage(text, relId, relName, reqType)
                            },
                            onClearChatHistory = {
                                viewModel.clearChatHistory()
                            }
                        )
                    }

                    AppNavSection.LINK_DIRECTORY -> {
                        LinkDirectoryScreen(
                            links = networkLinks,
                            onOpenLink = { url -> viewModel.openInInAppBrowser(url) },
                            onAddLink = { title, url, category, desc ->
                                viewModel.addCustomNetworkLink(title, url, category, desc)
                            },
                            onDeleteLink = { link -> viewModel.deleteNetworkLink(link) }
                        )
                    }

                    AppNavSection.TECHNICIAN_LOGS -> {
                        TechnicianLogsScreen(
                            logs = technicianLogs,
                            userRole = userRole,
                            onSubmitLog = { clientName, siteUrl, equipmentModel, signalDbm, status, notes ->
                                viewModel.submitTechnicianLog(
                                    clientName,
                                    siteUrl,
                                    equipmentModel,
                                    signalDbm,
                                    status,
                                    notes
                                )
                            },
                            onDeleteLog = { logItem -> viewModel.deleteTechnicianLog(logItem) }
                        )
                    }

                    AppNavSection.ADMIN_PANEL -> {
                        AdminPanelScreen(
                            userRole = userRole,
                            totalLinksCount = networkLinks.size,
                            totalSearchCount = searchHistory.size,
                            totalLogsCount = technicianLogs.size,
                            onUpdateAdminPassword = { newPassword ->
                                viewModel.updateAdminPassword(newPassword)
                            },
                            onClearHistory = { viewModel.clearSearchHistory() }
                        )
                    }

                    AppNavSection.WEB_VIEWER -> {
                        selectedWebUrl?.let { webUrl ->
                            InAppWebViewerScreen(
                                url = webUrl,
                                onClose = { viewModel.setNavSection(AppNavSection.DASHBOARD_SEARCH) }
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
