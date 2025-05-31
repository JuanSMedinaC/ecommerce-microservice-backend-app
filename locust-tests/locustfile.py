from locust import HttpUser, task, between
import random
import uuid

class EcommerceUser(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        self.category = {
            "categoryId": 1,
            "categoryTitle": "Default Category"
        }

    def generate_product(self):
        return {
            "productTitle": "Book " + str(uuid.uuid4())[:6],
            "priceUnit": 19.99,
            "sku": "SKU-" + str(random.randint(100, 999)),
            "imageUrl": "https://example.com",
            "quantity": random.randint(1, 5),
            "categoryDto": self.category
        }

    @task(2)
    def list_products(self):
        self.client.get("/api/products")

    @task(1)
    def create_product(self):
        product = self.generate_product()
        response = self.client.post("/api/products", json=product)
        if response.status_code == 200:
            self.last_product = response.json()

    @task(1)
    def get_product_by_id(self):
        if hasattr(self, "last_product"):
            product_id = self.last_product.get("productId")
            self.client.get(f"/api/products/{product_id}")

    @task(1)
    def update_product(self):
        if hasattr(self, "last_product"):
            product = self.last_product
            product["productTitle"] = "Updated " + product["productTitle"]
            self.client.put("/api/products", json=product)

    @task(1)
    def delete_product(self):
        if hasattr(self, "last_product"):
            product_id = self.last_product.get("productId")
            self.client.delete(f"/api/products/{product_id}")

    @task(1)
    def create_order(self):
        cart = {"userId": 1, "cartId": 1}
        order = {
            "orderDesc": "Locust load test order",
            "orderFee": random.uniform(50, 200),
            "cartDto": cart
        }
        self.client.post("/api/orders", json=order)
