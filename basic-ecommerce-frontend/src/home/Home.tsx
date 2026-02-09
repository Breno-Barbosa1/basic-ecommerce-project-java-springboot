import { useNavigate} from 'react-router-dom';
import { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import { useCartContext } from '../context/CartContext';

import "./Home.css"

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
}

interface CartItem {
  id: number;
  productDTO: Product;
  quantity: number;
}

export default function Home() {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLogged, setLoggedIn] = useState(false);
  const { cart, addToCart, fetchCart } = useCartContext();

  const navigate = useNavigate();

  const isTokenValid = (token: string): boolean => {
    if (!token) return false;
    try {
      const decodedToken: any = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      return decodedToken.exp > currentTime;
    } catch (error) {
      console.error('Error decoding token:', error);
      return false;
    }
  };

  const handleLogOut = async (e: any) => {
    e.preventDefault();

    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
    navigate("/login");
  };

  useEffect(() => {
    const fetchData = async() => {

      const token = localStorage.getItem("token");

      const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

      if (token && isTokenValid(token)) {
        setLoggedIn(true);
      } else {
        navigate("/login");
        return;
      }

      try {
        const response = await fetch(`${API_URL}/api/products`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
        });
        if (response.ok) {
          const data = await response.json();
          setProducts(data);
        } else {
          console.error('Failed to fetch products. Status:', response.status);
        }
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    }

    fetchData();
  }, []);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  return (
    <div className="home-page">
      <nav className="home-nav">
        <div className='nav-left'>
          <h1 className="brand">
            <svg className="brand-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <path d="M6 6h15l-1.5 9h-11L6 6z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" strokeLinecap="round" />
              <circle cx="10" cy="19" r="1" fill="currentColor" />
              <circle cx="18" cy="19" r="1" fill="currentColor" />
            </svg>
            Java Marketplace
          </h1>
        </div>
        <div className='nav-right'>
          <button className="nav-buttons" onClick={(e) => handleLogOut(e)}>Log Out</button>
          <button className="nav-buttons cart-btn" onClick= {() => navigate("/my-cart")}>
            <span>
              {cart.length}
            </span>
            <svg className="cart-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <path d="M6 6h15l-1.5 9h-11L6 6z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" strokeLinecap="round" />
              <circle cx="10" cy="19" r="1" fill="currentColor" />
              <circle cx="18" cy="19" r="1" fill="currentColor" />
            </svg>
            My Cart
          </button>
        </div>
      </nav>
        <main className="product-list">
          {products.map((product) => {
            const itemInCart = cart.find((item) => item.productDTO.id === product.id);
            const cartQuantity = itemInCart ? itemInCart.quantity : 0;
            const remainingStock = product.stockQuantity - cartQuantity;

    return (
      <div key={product.id} className="product-card">
        <h2 className="product-name">{product.name}</h2>
        <p className="product-description">{product.description}</p>
        <p className="product-price">${product.price.toFixed(2)}</p>
        <p className="product-stock">In Stock: {remainingStock}</p>

        {remainingStock > 0 ? (
          <button 
            className="buy-now-btn" 
            onClick={() => addToCart(product)}
          >
            Add to Cart
          </button>
        ) : (
        <button className="buy-now-btn-disabled">
          Out of Stock
        </button>
      )}
      </div>
        );
      })}
        </main>
      </div>
  );
};