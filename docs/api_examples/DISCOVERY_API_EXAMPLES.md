# Discovery API Examples - TheMealDB

Đây là các ví dụ món ăn có thể lấy từ TheMealDB API trong phần Discovery của FoodMoodDiary.

## 1. Món Gà (Chicken)

### Chicken Handi
- **ID:** 52795
- **Category:** Chicken
- **Area:** Indian
- **Image:** https://www.themealdb.com/images/media/meals/wyxwsp1486979827.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=52795`

![Chicken Handi](https://www.themealdb.com/images/media/meals/wyxwsp1486979827.jpg)

### Chicken Mandi
- **ID:** 53358
- **Category:** Chicken
- **Area:** Indian
- **Image:** https://www.themealdb.com/images/media/meals/er4d081765186828.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53358`

![Chicken Mandi](https://www.themealdb.com/images/media/meals/er4d081765186828.jpg)

### Sticky Chicken
- **ID:** 53110
- **Category:** Chicken
- **Area:** Australian
- **Image:** https://www.themealdb.com/images/media/meals/cj56fs1762340001.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53110`

![Sticky Chicken](https://www.themealdb.com/images/media/meals/cj56fs1762340001.jpg)

---

## 2. Hải Sản (Seafood)

### Arroz con gambas y calamar
- **ID:** 53147
- **Category:** Seafood
- **Area:** Spanish
- **Image:** https://www.themealdb.com/images/media/meals/jc6oub1763196663.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53147`

![Arroz con gambas](https://www.themealdb.com/images/media/meals/jc6oub1763196663.jpg)

### Baked salmon with fennel
- **ID:** 52959
- **Category:** Seafood
- **Area:** British
- **Image:** https://www.themealdb.com/images/media/meals/1548772327.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=52959`

![Baked salmon](https://www.themealdb.com/images/media/meals/1548772327.jpg)

### Chilli prawn linguine
- **ID:** 52839
- **Category:** Seafood/Pasta
- **Area:** Italian
- **Image:** https://www.themealdb.com/images/media/meals/usywpp1511189717.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=52839`

![Chilli prawn linguine](https://www.themealdb.com/images/media/meals/usywpp1511189717.jpg)

---

## 3. Pasta

### Fettuccine Alfredo
- **ID:** 53064
- **Category:** Pasta
- **Area:** Italian
- **Image:** https://www.themealdb.com/images/media/meals/0jv5gx1661040802.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53064`

![Fettuccine Alfredo](https://www.themealdb.com/images/media/meals/0jv5gx1661040802.jpg)

---

## 4. Dessert

### Æbleskiver (Danish Pancakes)
- **ID:** 53120
- **Category:** Dessert
- **Area:** Danish
- **Image:** https://www.themealdb.com/images/media/meals/wkhg581762773124.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53120`

![Æbleskiver](https://www.themealdb.com/images/media/meals/wkhg581762773124.jpg)

### Alfajores
- **ID:** 53138
- **Category:** Dessert
- **Area:** Argentinian
- **Image:** https://www.themealdb.com/images/media/meals/a4kgf21763075288.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53138`

![Alfajores](https://www.themealdb.com/images/media/meals/a4kgf21763075288.jpg)

### Date squares
- **ID:** 53338
- **Category:** Dessert
- **Area:** Canadian
- **Image:** https://www.themealdb.com/images/media/meals/u4scbo1764446514.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53338`

![Date squares](https://www.themealdb.com/images/media/meals/u4scbo1764446514.jpg)

---

## 5. Bò (Beef)

### Algerian Kefta (Meatballs)
- **ID:** 53281
- **Category:** Beef
- **Area:** Algerian
- **Image:** https://www.themealdb.com/images/media/meals/8rfd4q1764112993.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53281`

![Algerian Kefta](https://www.themealdb.com/images/media/meals/8rfd4q1764112993.jpg)

### Arepa Pabellón
- **ID:** 53334
- **Category:** Beef
- **Area:** Venezuelan
- **Image:** https://www.themealdb.com/images/media/meals/13fg4j1764441982.jpg
- **API:** `https://www.themealdb.com/api/json/v1/1/lookup.php?i=53334`

![Arepa Pabellón](https://www.themealdb.com/images/media/meals/13fg4j1764441982.jpg)

---

## Các API endpoints hữu ích:

### 1. Random Meal (Món ngẫu nhiên)
```
https://www.themealdb.com/api/json/v1/1/random.php
```

### 2. Search by name (Tìm theo tên)
```
https://www.themealdb.com/api/json/v1/1/search.php?s=chicken
```

### 3. Filter by category (Lọc theo loại)
```
https://www.themealdb.com/api/json/v1/1/filter.php?c=Seafood
```

### 4. Filter by area (Lọc theo vùng)
```
https://www.themealdb.com/api/json/v1/1/filter.php?a=Italian
```

### 5. Lookup by ID (Chi tiết món ăn)
```
https://www.themealdb.com/api/json/v1/1/lookup.php?i=52772
```

### 6. List all categories (Danh sách categories)
```
https://www.themealdb.com/api/json/v1/1/categories.php
```

### 7. List all areas (Danh sách khu vực)
```
https://www.themealdb.com/api/json/v1/1/list.php?a=list
```

---

## Cách sử dụng trong app:

1. **Tab "Khám phá món ăn"**: Sử dụng random API để load món ăn ngẫu nhiên
2. **Category Filters**: Sử dụng filter by category API
3. **Favorite/Save**: Lưu vào local database hoặc Firebase
4. **Chi tiết món ăn**: Hiển thị ingredients, instructions, YouTube video

## Response Structure:

```json
{
  "meals": [
    {
      "idMeal": "52795",
      "strMeal": "Chicken Handi",
      "strCategory": "Chicken",
      "strArea": "Indian",
      "strInstructions": "...",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/wyxwsp1486979827.jpg",
      "strYoutube": "https://www.youtube.com/watch?v=IO0issT0Rmc",
      "strIngredient1": "Chicken",
      "strMeasure1": "1.2 kg",
      ...
    }
  ]
}
```

---

**Note:** Tất cả hình ảnh đều có sẵn và có thể load trực tiếp từ URL. API hoàn toàn miễn phí và không yêu cầu authentication.
