from locust import HttpUser, task, between
from datetime import datetime
import random
import uuid

class EcommerceUser(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        self.category = {
            "categoryId": 1,
            "categoryTitle": "Default Category"
        }
        self.last_product = None
        self.username = "newname"

    def generate_product(self):
        return {
            "productTitle": "Book " + str(uuid.uuid4())[:6],
            "priceUnit": round(random.uniform(10, 100), 2),
            "sku": "SKU-" + str(random.randint(100, 999)),
            "imageUrl": "https://example.com/image.jpg",
            "quantity": random.randint(1, 10),
            "category": self.category
        }

    @task(2)
    def list_products(self):
        with self.client.get("/app/api/products", catch_response=True) as response:
            if response.status_code != 200:
                response.failure(f"Failed to list products: {response.status_code}")

    @task(2)
    def create_product(self):
        product = self.generate_product()
        with self.client.post("/app/api/products", json=product, catch_response=True) as response:
            if response.status_code == 200:
                self.last_product = response.json()
            else:
                response.failure(f"Failed to create product: {response.status_code} - {response.text}")

    @task(1)
    def get_product_by_id(self):
        if self.last_product:
            product_id = self.last_product.get("productId")
            with self.client.get(f"/app/api/products/{product_id}", catch_response=True) as response:
                if response.status_code != 200:
                    response.failure(f"Failed to fetch product {product_id}: {response.status_code}")
