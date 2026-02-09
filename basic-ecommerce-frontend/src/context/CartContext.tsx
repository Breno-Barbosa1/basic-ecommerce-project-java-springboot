import { createContext, useContext, useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

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

interface CartContextType {
  cart: CartItem[];
  addToCart: (product: Product) => void;
  cartTotal: number;
  removeFromCart: (CartItemId: number) => void;
  clearCart: () => void;
  checkout: () => Promise<void>;
  fetchCart: () => Promise<CartItem[]>;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{children: React.ReactNode}> = ({children}) => {
    const [cart, setCart] = useState<CartItem[]>([]);

    const cartTotal = useMemo(() => {
      return cart.reduce((acc, item) => {
        const price = item.productDTO?.price || 0;
        return acc + (price * item.quantity);
      }, 0);
    }, [cart])

    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

    const addToCart = useCallback(async (product: Product) => {
        const token = localStorage.getItem("token");
        const userEmail = localStorage.getItem("userEmail");

        if (!token || !userEmail) {
          setCart([]);
          return; 
        }

        try {
          const response = await fetch(`${API_URL}/api/carts/update`, {
          method: "PUT",
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(
          { 
            "email": userEmail,
            "item": {
              "productDTO": {
                "id": product.id,
                "name": product.name,
                "description": product.description,
                "price": product.price,
                "stockQuantity": product.stockQuantity
              },
              "quantity": 1
            }
          }
          )
        });
        if (response.ok) {
          const data = await response.json();
          setCart(data.items);
        } else {
          console.error("Failed to update cart. Status: " + response.status)
        }
      } catch (error) {
          console.error("Error while updating cart: " + error)
      }
    }, []);
    
    const fetchCart = useCallback(async (): Promise<CartItem[]> => {
      const token = localStorage.getItem("token");
      const userEmail = localStorage.getItem("userEmail");

      if (!token || !userEmail) {
        setCart([]);
        return []; 
      }

      try {
        const response = await fetch(`${API_URL}/api/carts/${userEmail}`, {
          method: "GET",
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        });
        if (response.ok) {
          const data = await response.json();
          setCart(data.items);
          return data.items;
        } else if (response.status === 401) {
          localStorage.removeItem("token");
          setCart([]);
          
        }
      } catch (error) {
        console.error("Error while retrieving cart:", error);
      }
      return [];
    }, []);

    const removeFromCart = useCallback(async (CartItemId: number) => {
      const token = localStorage.getItem("token");
      const userEmail = localStorage.getItem("userEmail");

      if (!token || !userEmail) {
          setCart([]);
          return; 
        }

      try {
          const response = await fetch(`${API_URL}/api/carts/remove/${userEmail}/${CartItemId}`, {
            method: "DELETE",
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`
            }
        });
        if (response.ok) {
          const data = await response.json();
          setCart(data.items);
        } else {
          console.error("Failed to update cart. Status: " + response.status)
        }
      } catch (error) {
          console.error("Error while updating cart: " + error)
      }
    }, []);

    const clearCart = useCallback(async () => {
      const token = localStorage.getItem("token");
      const userEmail = localStorage.getItem("userEmail");

      if (token == null || userEmail == null) return;

      try {
        const response = await fetch(`${API_URL}/api/carts/${userEmail}`, {
            method: "DELETE",
            headers: {
              'Authorization': `Bearer ${token}`
            }
        });
        if (response.ok) {
          setCart([]);
        } else {
          console.error("Error clearing cart! status: " + response.status)
        }

      } catch(error) {
        console.error("Error clearing cart! error: " + error)
      }
    }, []);

    const checkout = useCallback(async () => {
      const token = localStorage.getItem("token");
      const userEmail = localStorage.getItem("userEmail");

      if (!token || !userEmail) {
          setCart([]);
          return; 
        }

      try {
        const response = await fetch(`${API_URL}/api/carts/checkout/${userEmail}`, {
          method: "POST",
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(
            {
              "email": userEmail,
              "items": cart
            }
          )
        }
        )
        if (response.ok) {
          setCart([]);
          alert("Order completed successfully!")
          navigate("/home")
        }
      } catch (error) {
        console.error("Error during checkout: " + error);
      }
    }, [cart, navigate]);
    
    return (
      <CartContext.Provider value={{ cart, fetchCart, addToCart, checkout, cartTotal, removeFromCart, clearCart }}>
        {children}
      </CartContext.Provider>
    );
};

export const useCartContext = () => {
    const context = useContext(CartContext);
    if (!context) throw new Error('useCart must be used within a CartProvider');
    return context;
};