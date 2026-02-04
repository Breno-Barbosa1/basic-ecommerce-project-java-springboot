import "./Cart.css";

import { useCartContext } from "../context/CartContext";
import { useEffect } from "react";

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

export default function Cart() {
  const { cart, fetchCart, checkout, removeFromCart, clearCart } = useCartContext();

  const handleCheckout = async (e: React.FormEvent) => {
    e.preventDefault();
    
    await checkout(); 
  };

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  return (
    <div className="cart-page">
      <nav className="cart-nav">
        <div className="left-nav">
          <h1 className="brand">
          <svg className="brand-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
            <path d="M6 6h15l-1.5 9h-11L6 6z" stroke="currentColor" strokeWidth="1.4" strokeLinejoin="round" strokeLinecap="round" />
            <circle cx="10" cy="19" r="1" fill="currentColor" />
            <circle cx="18" cy="19" r="1" fill="currentColor" />
          </svg>
            My Cart
          </h1>
    </div>
        <div className="right-nav">
          <button className="nav-buttons" onClick={() => window.history.back()}>Home</button>
        </div>
      </nav>
            {cart.length === 0 ? (
            <div className="cart-content">  
                <h2>Your Shopping Cart is Empty</h2>
                <p>Add items to your cart to see them here.</p>
            </div>
              ) : (
                <div className="cart-content">
                  <h2>Your Shopping Cart</h2>
                  <div className="cart-items">
                    {cart.map((item: CartItem) => (
                      <div key={item.productDTO.id} className="cart-item">
                        <h3 className="cart-item-name">{item.productDTO?.name || "Product Name Unavailable"}</h3>
                        <p className="cart-item-description">{item.productDTO?.description}</p>
                        <p className="cart-item-price">Price: ${item.productDTO?.price?.toFixed(2)}</p>
                        <p className="cart-item-quantity">Quantity: {item.quantity}</p>
                        <p className="cart-item-subtotal">Subtotal: ${(item.productDTO?.price * item.quantity).toFixed(2)}</p>
                        <button className="remove-item-btn" onClick={() => removeFromCart(item.id)}>Remove</button>
                      </div>
                    ))}
                  </div>
                  <div className="cart-summary">
                    <h3>Order Summary</h3>
                    <div className="summary-details">
                      <p>Total Items: {cart.reduce((sum: number, item: CartItem) => sum + item.quantity, 0)}</p>
                      <p className="total-price">
                        Total Amount: ${cart.reduce((total: number, item: CartItem) => total + (item.productDTO.price * item.quantity), 0).toFixed(2)}
                      </p>
                    </div>
                    <button className="checkout-btn" onClick={(e) => handleCheckout(e)}>
                      Place Order
                    </button>
                    <button className="clear-btn" onClick={() => clearCart()}>
                      Clear Cart
                    </button>
                  </div>
                </div>
              ) }
            </div>
        );
      }