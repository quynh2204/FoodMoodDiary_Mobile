#!/usr/bin/env python3
"""
Script to upload Vietnamese meal data to Firestore

Prerequisites:
1. Install Firebase Admin SDK: pip install firebase-admin
2. Download service account key from Firebase Console:
   - Go to Project Settings > Service Accounts
   - Click "Generate New Private Key"
   - Save as serviceAccountKey.json in this directory

Usage:
    python upload_meals_to_firestore.py
"""

import firebase_admin
from firebase_admin import credentials, firestore

# Initialize Firebase Admin SDK
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred)

db = firestore.client()

# Vietnamese meals data
meals = [
    # ═══════════════════════════════════════════
    # Món nước (Soup/Noodle dishes)
    # ═══════════════════════════════════════════
    {
        "id": "pho_bo",
        "name": "Phở bò",
        "category": "Món nước",
        "youtubeUrl": "https://www.youtube.com/watch?v=99tOr7JSr0k",
        "imageUrl": "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?q=80&w=1674&auto=format&fit=crop",
        "calories": 350,
        "description": "Món phở truyền thống của Việt Nam với nước dùng thơm ngon từ xương bò",
        "tags": ["breakfast", "popular", "noodles", "beef"]
    },
    {
        "id": "bun_bo_hue",
        "name": "Bún bò Huế",
        "category": "Món nước",
        "youtubeUrl": "https://www.youtube.com/watch?v=A_o2qfaTgKs",
        "imageUrl": "https://images.unsplash.com/photo-1597345637412-9fd611e758f3?q=80&w=2070&auto=format&fit=crop",
        "calories": 450,
        "description": "Món bún cay đặc trưng của xứ Huế",
        "tags": ["spicy", "noodles", "beef", "central-vietnam"]
    },
    {
        "id": "bun_rieu",
        "name": "Bún riêu cua",
        "category": "Món nước",
        "youtubeUrl": "https://www.youtube.com/watch?v=CiPDEMxUfTY",
        "imageUrl": "https://cdn.tgdd.vn/2020/08/CookProduct/Untitled-1-1200x676-10.jpg",
        "calories": 320,
        "description": "Món bún với nước dùng cà chua và riêu cua đậm đà",
        "tags": ["crab", "noodles", "tomato"]
    },
    {
        "id": "mi_quang",
        "name": "Mì Quảng",
        "category": "Món nước",
        "youtubeUrl": "https://www.youtube.com/watch?v=g3V_oNeMdHs",
        "imageUrl": "https://daivietourist.vn/wp-content/uploads/2025/06/mi-quang-hoi-an-2.jpg",
        "calories": 400,
        "description": "Đặc sản Quảng Nam với sợi mì vàng đặc trưng",
        "tags": ["quang-nam", "noodles", "pork", "shrimp"]
    },
    {
        "id": "hu_tieu",
        "name": "Hủ tiếu Nam Vang",
        "category": "Món nước",
        "youtubeUrl": "https://www.youtube.com/watch?v=fziqSn-xkws",
        "imageUrl": "https://i-giadinh.vnecdn.net/2023/05/15/Buoc-8-Thanh-pham-1-8-8366-1684125654.jpg",
        "calories": 380,
        "description": "Món hủ tiếu có nguồn gốc từ Nam Vang, Campuchia",
        "tags": ["breakfast", "noodles", "pork", "southern-vietnam"]
    },
    {
        "id": "banh_canh",
        "name": "Bánh canh cua",
        "category": "Món nước",
        "youtubeUrl": "https://www.youtube.com/watch?v=uTIZmap4UnQ",
        "imageUrl": "https://cdn.tgdd.vn/2021/05/CookProduct/thumbcmscn-1200x676-4.jpg",
        "calories": 350,
        "description": "Bánh canh dai ngon với nước dùng cua ngọt thanh",
        "tags": ["crab", "thick-noodles", "seafood"]
    },
    {
        "id": "bun_cha",
        "name": "Bún chả Hà Nội",
        "category": "Món nước",
        "youtubeUrl": "https://www.youtube.com/watch?v=tcbSm6nSUhM",
        "imageUrl": "https://cdn.zsoft.solutions/poseidon-web/app/media/uploaded-files/090724-bun-cha-ha-noi-buffet-poseidon-1.jpeg",
        "calories": 420,
        "description": "Đặc sản Hà Nội với chả nướng thơm lừng",
        "tags": ["hanoi", "grilled-pork", "northern-vietnam", "vermicelli"]
    },
    {
        "id": "bun_thit_nuong",
        "name": "Bún thịt nướng",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=emlDazeUIMM",
        "imageUrl": "https://cooponline.vn/tin-tuc/wp-content/uploads/2025/10/cach-lam-bun-thit-nuong-chuan-vi-sai-gon-thom-ngon-dam-da-kho-cuong.png",
        "calories": 480,
        "description": "Bún với thịt nướng thơm phức",
        "tags": ["grilled-pork", "vermicelli", "southern-vietnam"]
    },

    # ═══════════════════════════════════════════
    # Món khô (Main dishes)
    # ═══════════════════════════════════════════
    {
        "id": "com_tam",
        "name": "Cơm tấm sườn bì chả",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=OVb5uoDWspM",
        "imageUrl": "https://i-giadinh.vnecdn.net/2024/03/07/7Honthinthnhphm1-1709800144-8583-1709800424.jpg",
        "calories": 550,
        "description": "Món cơm tấm đặc trưng của Sài Gòn",
        "tags": ["rice", "pork", "southern-vietnam", "popular"]
    },
    {
        "id": "banh_mi",
        "name": "Bánh mì thịt",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=5dX-hXxKM8E",
        "imageUrl": "https://www.huongnghiepaau.com/wp-content/uploads/2019/08/banh-mi-kep-thit-nuong-thom-phuc.jpg",
        "calories": 400,
        "description": "Bánh mì Việt Nam nổi tiếng thế giới",
        "tags": ["breakfast", "bread", "street-food", "popular"]
    },
    {
        "id": "goi_cuon",
        "name": "Gỏi cuốn tôm thịt",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=LJ_3BeqH63w",
        "imageUrl": "https://cdn.netspace.edu.vn/images/2020/04/25/cach-lam-goi-cuon-tom-thit-cuc-ki-hap-dan-245587-800.jpg",
        "calories": 180,
        "description": "Gỏi cuốn tươi mát với tôm và thịt",
        "tags": ["fresh", "healthy", "appetizer", "shrimp"]
    },
    {
        "id": "bo_luc_lac",
        "name": "Bò lúc lắc",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=0X5m98q3Pn0",
        "imageUrl": "https://cdn.netspace.edu.vn/images/2020/04/28/cach-lam-bo-luc-lac-ngon-244971-800.jpg",
        "calories": 520,
        "description": "Bò xào kiểu Pháp - Việt đặc biệt",
        "tags": ["beef", "stir-fry", "french-fusion"]
    },
    {
        "id": "ca_kho_to",
        "name": "Cá kho tộ",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=zvlct2ZXhj8",
        "imageUrl": "https://cdnv2.tgdd.vn/mwg-static/common/Common/05052025%20-%202025-05-09T154044.858.jpg",
        "calories": 350,
        "description": "Món cá kho đậm đà hương vị miền Nam",
        "tags": ["fish", "braised", "southern-vietnam", "rice-dish"]
    },
    {
        "id": "thit_kho_trung",
        "name": "Thịt kho trứng",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=Ef2KDf7x1BY",
        "imageUrl": "https://cdn.tgdd.vn/Files/2017/03/28/965845/cach-lam-thit-kho-trung-5_760x450.jpg",
        "calories": 480,
        "description": "Món ăn truyền thống cho ngày Tết",
        "tags": ["pork", "egg", "braised", "traditional"]
    },
    {
        "id": "bun_dau",
        "name": "Bún đậu mắm tôm",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=wiy_GKcrkxk",
        "imageUrl": "https://cdn.tgdd.vn/2021/12/CookRecipe/GalleryStep/thanh-pham-1032.jpg",
        "calories": 450,
        "description": "Món ăn đậm chất Hà Nội với mắm tôm đặc trưng",
        "tags": ["hanoi", "tofu", "fermented-shrimp", "northern-vietnam"]
    },
    {
        "id": "com_suon",
        "name": "Cơm sườn nướng",
        "category": "Món khô",
        "youtubeUrl": "https://www.youtube.com/watch?v=cJu6tFJe_Gc",
        "imageUrl": "https://cdn.tgdd.vn/2021/08/CookProduct/t1-1200x676.jpg",
        "calories": 520,
        "description": "Cơm với sườn nướng thơm ngon",
        "tags": ["rice", "grilled-pork", "popular"]
    },

    # ═══════════════════════════════════════════
    # Tráng miệng (Desserts)
    # ═══════════════════════════════════════════
    {
        "id": "che_ba_mau",
        "name": "Chè ba màu",
        "category": "Tráng miệng",
        "youtubeUrl": "https://www.youtube.com/watch?v=x9pUWnYw470",
        "imageUrl": "https://helenrecipes.com/wp-content/uploads/2015/03/IMG_9091.jpg",
        "calories": 280,
        "description": "Chè nhiều màu sắc với đậu đỏ, đậu xanh và thạch",
        "tags": ["sweet", "colorful", "traditional", "cold"]
    },
    {
        "id": "banh_flan",
        "name": "Bánh flan caramen",
        "category": "Tráng miệng",
        "youtubeUrl": "https://www.youtube.com/watch?v=xBCp5-bzfZQ",
        "imageUrl": "https://chefdzung.com.vn/uploads/images/ngoc-linh/banh-caramen.jpg",
        "calories": 220,
        "description": "Bánh flan mềm mịn với caramel ngọt ngào",
        "tags": ["sweet", "french-fusion", "pudding"]
    },
    {
        "id": "banh_da_lon",
        "name": "Bánh da lợn",
        "category": "Tráng miệng",
        "youtubeUrl": "https://www.youtube.com/watch?v=8_xg7sOx3ys",
        "imageUrl": "https://bactom.com/wp-content/uploads/2024/11/2-6.jpg",
        "calories": 180,
        "description": "Bánh nhiều lớp màu sắc với vị đậu xanh thơm",
        "tags": ["sweet", "layered", "traditional", "mung-bean"]
    },
    {
        "id": "banh_troi",
        "name": "Bánh trôi bánh chay",
        "category": "Tráng miệng",
        "youtubeUrl": "https://www.youtube.com/watch?v=hVa8e9zQI6U",
        "imageUrl": "https://cdn.tgdd.vn/2021/03/CookRecipe/GalleryStep/tha%CC%80nh-pha%CC%89m-29.jpg",
        "calories": 150,
        "description": "Bánh trôi nước ngọt thanh cho ngày Tết Hàn thực",
        "tags": ["sweet", "glutinous-rice", "traditional", "festival"]
    },
    {
        "id": "banh_xeo",
        "name": "Bánh xèo miền Tây",
        "category": "Tráng miệng",
        "youtubeUrl": "https://www.youtube.com/watch?v=GOUmS6kRoGw",
        "imageUrl": "https://i-giadinh.vnecdn.net/2023/09/19/Buoc-10-Thanh-pham-1-1-5225-1695107554.jpg",
        "calories": 380,
        "description": "Bánh xèo giòn tan với nhân tôm thịt",
        "tags": ["crispy", "southern-vietnam", "savory", "popular"]
    },
    {
        "id": "banh_beo",
        "name": "Bánh bèo Huế",
        "category": "Tráng miệng",
        "youtubeUrl": "https://www.youtube.com/watch?v=rafBOn2wOS0",
        "imageUrl": "https://daylambanh.edu.vn/wp-content/uploads/2017/08/luu-y-khi-lam-banh-beo-hue.jpg",
        "calories": 120,
        "description": "Bánh bèo mềm mịn với tôm khô và mỡ hành",
        "tags": ["central-vietnam", "hue", "steamed", "small-bite"]
    }
]

def upload_meals():
    """Upload all meals to Firestore"""
    collection_ref = db.collection('vietnameseMeals')
    
    success_count = 0
    error_count = 0
    
    for meal in meals:
        try:
            meal_id = meal.pop('id')  # Remove id from data, use as document ID
            collection_ref.document(meal_id).set(meal)
            print(f"✓ Uploaded: {meal['name']} (ID: {meal_id})")
            success_count += 1
        except Exception as e:
            print(f"✗ Error uploading {meal.get('name', 'Unknown')}: {str(e)}")
            error_count += 1
    
    print(f"\n{'='*50}")
    print(f"Upload complete!")
    print(f"Success: {success_count}")
    print(f"Errors: {error_count}")
    print(f"{'='*50}")

if __name__ == "__main__":
    print("Starting Vietnamese meals upload to Firestore...\n")
    upload_meals()
