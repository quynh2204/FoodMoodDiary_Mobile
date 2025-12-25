package com.haphuongquynh.foodmooddiary.presentation.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haphuongquynh.foodmooddiary.ui.theme.*

@Composable
fun EmptyStateScreen(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = WhiteText,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = description,
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelGreen,
                    contentColor = BlackPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NoEntriesEmptyState(
    onAddEntryClick: () -> Unit
) {
    EmptyStateScreen(
        icon = Icons.Default.Restaurant,
        title = "Chưa có nhật ký nào",
        description = "Bắt đầu ghi nhận bữa ăn đầu tiên của bạn bằng cách chụp ảnh món ăn và thêm cảm xúc",
        actionText = "Thêm bữa ăn đầu tiên",
        onActionClick = onAddEntryClick
    )
}

@Composable
fun NoStatisticsEmptyState() {
    EmptyStateScreen(
        icon = Icons.Default.BarChart,
        title = "Chưa có thống kê",
        description = "Ghi nhật ký thường xuyên để xem các biểu đồ và phân tích về thói quen ăn uống của bạn"
    )
}

@Composable
fun NoDiscoveryEmptyState(
    onRetryClick: () -> Unit
) {
    EmptyStateScreen(
        icon = Icons.Default.SearchOff,
        title = "Không tìm thấy món ăn",
        description = "Thử tìm kiếm với từ khóa khác hoặc kiểm tra kết nối internet của bạn",
        actionText = "Thử lại",
        onActionClick = onRetryClick
    )
}

@Composable
fun NoMapLocationsEmptyState() {
    EmptyStateScreen(
        icon = Icons.Default.LocationOff,
        title = "Chưa có vị trí nào",
        description = "Bật định vị khi thêm bữa ăn để xem bản đồ các địa điểm bạn đã ghé thăm"
    )
}

@Composable
fun NoSearchResultsEmptyState(
    searchQuery: String
) {
    EmptyStateScreen(
        icon = Icons.Default.SearchOff,
        title = "Không tìm thấy \"$searchQuery\"",
        description = "Không có kết quả phù hợp với tìm kiếm của bạn. Hãy thử từ khóa khác"
    )
}

@Composable
fun ErrorState(
    title: String = "Đã xảy ra lỗi",
    description: String = "Không thể tải dữ liệu. Vui lòng thử lại sau",
    onRetryClick: () -> Unit
) {
    EmptyStateScreen(
        icon = Icons.Default.ErrorOutline,
        title = title,
        description = description,
        actionText = "Thử lại",
        onActionClick = onRetryClick
    )
}

@Composable
fun LoadingState(
    message: String = "Đang tải..."
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = PastelGreen,
            strokeWidth = 4.dp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoNotificationsEmptyState() {
    EmptyStateScreen(
        icon = Icons.Default.NotificationsOff,
        title = "Chưa có thông báo",
        description = "Bạn chưa có thông báo nào. Bật nhắc nhở để không bỏ lỡ các bữa ăn"
    )
}

@Composable
fun NetworkErrorState(
    onRetryClick: () -> Unit
) {
    EmptyStateScreen(
        icon = Icons.Default.WifiOff,
        title = "Không có kết nối",
        description = "Vui lòng kiểm tra kết nối internet và thử lại",
        actionText = "Thử lại",
        onActionClick = onRetryClick
    )
}

@Composable
fun PermissionDeniedState(
    title: String = "Cần quyền truy cập",
    description: String = "Ứng dụng cần quyền này để hoạt động. Vui lòng cấp quyền trong cài đặt",
    onSettingsClick: () -> Unit
) {
    EmptyStateScreen(
        icon = Icons.Default.Block,
        title = title,
        description = description,
        actionText = "Mở cài đặt",
        onActionClick = onSettingsClick
    )
}
