package com.haphuongquynh.foodmooddiary.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.DataManagementViewModel
import com.haphuongquynh.foodmooddiary.presentation.viewmodel.ExportState
import com.haphuongquynh.foodmooddiary.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: DataManagementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val exportState by viewModel.exportState.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    
    // Handle export state changes
    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportState.Success -> {
                Toast.makeText(context, "✅ ${state.format} exported successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is ExportState.Error -> {
                Toast.makeText(context, "❌ ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is ExportState.DataCleared -> {
                Toast.makeText(context, "🗑️ All data cleared successfully", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> { /* No action needed */ }
        }
    }
    var exportProgress by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý dữ liệu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = WhiteText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackPrimary,
                    titleContentColor = WhiteText
                )
            )
        },
        containerColor = BlackPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Export Section
            Text(
                text = "Xuất dữ liệu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteText
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sao lưu toàn bộ nhật ký món ăn của bạn",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Export Options
            ExportOptionCard(
                icon = Icons.Default.TableChart,
                title = "Xuất CSV",
                description = "Dữ liệu dạng bảng, dễ mở bằng Excel hoặc Google Sheets",
                fileSize = "~2.5 MB",
                onClick = { viewModel.exportToCSV(context) },
                color = PastelGreen
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ExportOptionCard(
                icon = Icons.Default.PictureAsPdf,
                title = "Xuất PDF",
                description = "Định dạng chuẩn, dễ chia sẻ và in ấn",
                fileSize = "~8.3 MB",
                onClick = { viewModel.exportToPDF(context) },
                color = ErrorRed
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ExportOptionCard(
                icon = Icons.Default.DataObject,
                title = "Xuất JSON",
                description = "Định dạng lập trình, phục hồi toàn bộ dữ liệu",
                fileSize = "~3.7 MB",
                onClick = { viewModel.exportToJSON(context) },
                color = Color(0xFF64B5F6)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Storage Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlackSecondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = null,
                            tint = PastelGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Dung lượng đã sử dụng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WhiteText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "245 MB / 500 MB",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LinearProgressIndicator(
                        progress = { 0.49f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PastelGreen,
                        trackColor = BlackTertiary,
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StorageItem(
                            icon = Icons.Default.Image,
                            label = "Ảnh",
                            size = "180 MB"
                        )
                        StorageItem(
                            icon = Icons.Default.DataObject,
                            label = "Dữ liệu",
                            size = "65 MB"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Danger Zone
            Text(
                text = "Vùng nguy hiểm",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ErrorRed
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Các hành động này không thể hoàn tác",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showClearDialog = true },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1810)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ErrorRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Xóa tất cả dữ liệu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Xóa vĩnh viễn toàn bộ nhật ký",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ErrorRed
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Clear All Data Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(56.dp)
                )
            },
            title = {
                Text(
                    "Xóa toàn bộ dữ liệu?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Hành động này sẽ xóa vĩnh viễn:",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Tất cả ảnh món ăn")
                    Text("• Toàn bộ nhật ký")
                    Text("• Thống kê và biểu đồ")
                    Text("• Cài đặt cá nhân")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Dữ liệu không thể khôi phục sau khi xóa!",
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Xóa tất cả")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun ExportOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    fileSize: String,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlackSecondary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = fileSize,
                    fontSize = 12.sp,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Icon(
                Icons.Default.Download,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun StorageItem(
    icon: ImageVector,
    label: String,
    size: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PastelGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Text(
                text = size,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = WhiteText
            )
        }
    }
}
