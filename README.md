# LonelyShop
LonelyShop là shop bán đồ thương hiệu có thể là thời trang, nội thất, vv... Đưa sản phẩm lên cho người dùng đặt hàng như Shopee.

Download link: https://www.mediafire.com/file/6srq5j71bfjl0b7/LonelyShop.apk/file 

# Ảnh demo

### Về chức năng đăng nhập cho người dùng: Đăng ký, đăng nhập, đăng xuất, quên mật khẩu
![image](https://github.com/user-attachments/assets/a10efc6b-9788-49db-a5ac-884ea06d67e6)


### Giao diện Shop: Tìm kiếm, thêm sản phẩm vào giỏ hàng, xem chi tiết sản phẩm, số lượng tăng giảm tùy thích, địa chỉ, xem trạng thái mua hàng
![image](https://github.com/user-attachments/assets/067e0704-403c-4cee-8753-24e22b56f045)


### Thay đổi thông tin hình ảnh tên
<p float="left">
  <img src="https://github.com/user-attachments/assets/7ce8a037-dbe4-4679-9d9d-0c767287df10" width="45%" />
</p>

<p float="right">
  <img src="https://github.com/user-attachments/assets/f1bb05d1-ab91-449b-8e98-b2ca004f90e4" width="45%" />
</p>

# Công cụ và thư viện đã dùng
- Navigation Component: Một Activity chứa nhiều Fragment thay vì tạo nhiều Activity. (Quản lý điều hướng giữa các phần của ứng dụng mà không cần tạo nhiều Activity.)
- Firebase Auth: Quản lý tài khoản, đăng nhập và đăng ký. (Dịch vụ xác thực của Firebase giúp quản lý việc đăng nhập và đăng ký người dùng.)
- Firebase Firestore: Cơ sở dữ liệu cho hệ thống. (Dịch vụ cơ sở dữ liệu thời gian thực của Firebase để lưu trữ và truy xuất dữ liệu.)
- Firebase Storage: Để lưu trữ hình ảnh sản phẩm và ảnh hồ sơ người dùng. (Dịch vụ lưu trữ của Firebase dùng để lưu trữ các tệp như hình ảnh và video.)
- MVVM & LiveData: Tách biệt mã logic khỏi giao diện và lưu trữ trạng thái trong trường hợp cấu hình màn hình thay đổi. (Mẫu thiết kế MVVM giúp tách biệt logic khỏi giao diện người dùng, và LiveData giúp theo dõi và lưu trữ trạng thái của dữ liệu.)
- Coroutines: Thực hiện một số mã trong nền. (Cung cấp cơ chế để thực thi các tác vụ bất đồng bộ và đồng thời.)
- View Binding: Thay vì làm mới giao diện bằng tay, view binding sẽ xử lý điều đó. (Tự động tạo các lớp liên kết để truy cập các view trong layout mà không cần gọi findViewById.)
- Glide: Tải và lưu cache hình ảnh trong ImageView. (Thư viện dùng để tải hình ảnh từ nguồn và hiển thị chúng trong các ImageView, đồng thời lưu trữ ảnh đã tải để cải thiện hiệu suất.)

