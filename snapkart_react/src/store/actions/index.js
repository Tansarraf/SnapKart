import toast from "react-hot-toast";
import api from "../../api/api";

export const fetchProducts = (queryString) => async (dispatch) => {
  try {
    dispatch({
      type: "IS_FETCHING",
    });
    const { data } = await api.get(`/public/products?${queryString}`);
    dispatch({
      type: "FETCH_PRODUCTS",
      payload: data.content,
      pageNumber: data.pageNumber,
      pageSize: data.pageSize,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      lastPage: data.lastPage,
    });
    localStorage.setItem("products", JSON.stringify(data.content));
    dispatch({
      type: "IS_SUCCESS",
    });
  } catch (error) {
    console.log(error);
    dispatch({
      type: "IS_ERROR",
      payload: error?.response?.data?.message || "Failed to fetch products",
    });
  }
};

export const fetchCategories = () => async (dispatch) => {
  try {
    dispatch({
      type: "CATEGORY_LOADER",
    });
    const { data } = await api.get(`/public/categories`);
    dispatch({
      type: "FETCH_CATEGORIES",
      payload: data.content,
      pageNumber: data.pageNumber,
      pageSize: data.pageSize,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      lastPage: data.lastPage,
    });
    dispatch({
      type: "IS_ERROR",
    });
  } catch (error) {
    console.log(error);
    dispatch({
      type: "IS_ERROR",
      payload: error?.response?.data?.message || "Failed to fetch categories",
    });
  }
};

export const addTocart =
  (data, qty = 1, toast) =>
    (dispatch, getState) => {
      // Finding the product
      const { products } = getState().products;
      const getProduct = products.find(
        (item) => item.productId === data.productId
      );
      // Check for the stock
      const isInStock = getProduct.quantity >= qty;
      // If in stock -> Add it else error
      if (isInStock) {
        dispatch({ type: "ADD_CART", payload: { ...data, quantity: qty } });
        toast.success(`${data?.productName} added to cart`);
        localStorage.setItem("cartItems", JSON.stringify(getState().carts.cart));
      } else {
        // error
        toast.error("Out of Stock");
      }
    };
export const increaseCartQty =
  (data, toast, currentQuantity, setCurrentQuantity) =>
    (dispatch, getState) => {
      const { products } = getState().products;
      const getProduct = products.find(
        (item) => item.productId === data.productId
      );

      const isInStock = getProduct.quantity >= currentQuantity + 1;
      if (isInStock) {
        const newQuantity = currentQuantity + 1;
        setCurrentQuantity(newQuantity);
        dispatch({
          type: "ADD_CART",
          payload: { ...data, quantity: newQuantity },
        });
        localStorage.setItem("cartItems", JSON.stringify(getState().carts.cart));
      } else {
        toast.error("Max limit reached");
      }
    };

export const decreaseCartQty = (data, newQuantity) => (dispatch, getState) => {
  dispatch({
    type: "ADD_CART",
    payload: { ...data, quantity: newQuantity },
  });
  localStorage.setItem("cartItems", JSON.stringify(getState().carts.cart));
};

export const removeFromCart = (data, toast) => (dispatch, getState) => {
  dispatch({ type: "REMOVE_CART", payload: data });
  toast.success(`${data.productName} removed from cart successfully`);
  localStorage.setItem("cartItems", JSON.stringify(getState().carts.cart));
};

export const authSigninUser =
  (sendData, toast, reset, navigate, setLoader) => async (dispatch) => {
    try {
      setLoader(true);
      const { data } = await api.post("/auth/signin", sendData);
      dispatch({
        type: "LOGIN_USER",
        payload: data,
      });
      localStorage.setItem("auth", JSON.stringify(data));
      reset();
      toast.success("Logged in successfully!");
      navigate("/");
    } catch (error) {
      console.log(error);
      toast.error(error?.response?.data?.message || "Internal Server Error");
    } finally {
      setLoader(false);
    }
  };

export const registerNewUser =
  (sendData, toast, reset, navigate, setLoader) => async (dispatch) => {
    try {
      setLoader(true);
      const { data } = await api.post("/auth/signup", sendData);
      reset();
      toast.success(data?.message || "You are registered successfully!");
      navigate("/login");
    } catch (error) {
      console.log(error);
      toast.error(
        error?.response?.data?.message ||
        error?.response?.data?.password ||
        "Internal Server Error"
      );
    } finally {
      setLoader(false);
    }
  };

export const logOutUser = (navigate) => (dispatch) => {
  dispatch({ type: "LOG_OUT" });
  localStorage.removeItem("auth");
  navigate("/login");
};

export const authGoogleLoginUser =
  (idToken, photoURL, displayName, toast, navigate, setLoader) =>
    async (dispatch) => {
      try {
        setLoader(true);

        const { data } = await api.post(
          "/auth/firebase-login",
          { idToken: idToken },
          { withCredentials: true }
        );
        const userWithGoogleDetails = {
          ...data.user,
          photoURL,
          displayName,
        };
        localStorage.setItem("authToken", data.token);
        localStorage.setItem("user", JSON.stringify(userWithGoogleDetails));

        dispatch({
          type: "AUTH_USER",
          payload: {
            ...data,
            user: userWithGoogleDetails,
          },
        });
        toast.success("Google login successful!");
        navigate("/");
      } catch (error) {
        console.error("Google login failed:", error);
        toast.error("Google login failed.");
      } finally {
        setLoader(false);
      }
    };

export const addOrUpdateAddress =
  (sendData, toast, addressId, setOpenAddressModal) =>
    async (dispatch, getState) => {
      // const { user } = getState().auth;
      // await api.post(`/addresses`, sendData, { headers: { Authorization: 'Bearer' + user.jwtToken } })
      dispatch({ type: "BUTTON_LOADER" });
      try {
        if (addressId) {
          const { data } = await api.put(`/addresses/${addressId}`, sendData);
        } else {
          const { data } = await api.post("/addresses", sendData);
        }
        dispatch(fetchUserAddresses());
        toast.success("Address saved successfully");
        dispatch({ type: "IS_SUCCESS" })
      } catch (error) {
        console.log(error);
        toast.error(error?.response?.data?.message || "Internal Server Error");
        dispatch({ type: "IS_ERROR", payload: null });
      } finally {
        setOpenAddressModal(false);
      }
    };


export const fetchUserAddresses = () => async (dispatch, getState) => {
  try {
    dispatch({
      type: "IS_FETCHING",
    });
    const { data } = await api.get(`/addresses`);
    dispatch({
      type: "USER_ADDRESS",
      payload: data
    });
    dispatch({
      type: "IS_SUCCESS",
    });
  } catch (error) {
    console.log(error);
    dispatch({
      type: "IS_ERROR",
      payload: error?.response?.data?.message || "Failed to fetch user addresses",
    });
  }
};

export const selectUserCheckoutAddress = (address) => {
  localStorage.setItem("CHECKOUT_ADDRESS", JSON.stringify(address));
  return {
    type: "SELECT_CHECKOUT_ADDRESS",
    payload: address
  }
}

export const clearCheckoutAddress = () => {
  return {
    type: "REMOVE_CHECKOUT_ADDRESS",
  }
}
export const deleteUserAddress =
  (toast, addressId, setOpenDeleteModal) =>
    async (dispatch, getState) => {
      try {
        dispatch({ type: "BUTTON_LOADER" });
        await api.delete(`/addresses/${addressId}`)
        dispatch({ type: "IS_SUCCESS" });
        dispatch(fetchUserAddresses());
        dispatch(clearCheckoutAddress());
        toast.success("Address deleted successfully");
      } catch (error) {
        console.log(error);
        dispatch({
          type: "IS_ERROR",
          payload: error?.response?.data?.message || "Some error occurred.Try again!"
        });
      } finally {
        setOpenDeleteModal(false);
      }
    }

export const addPaymentMethod = (method) => {
  return {
    type: "ADD_PAYMENT_METHOD",
    payload: method
  }
}

export const createUserCart = (sendCartItems) => async (dispatch, getState) => {
  try {
    dispatch({
      type: "IS_FETCHING",
    });
    await api.post('/cart/create', sendCartItems);
    await dispatch(getUserCart());
  } catch (error) {
    console.log(error);
    dispatch({
      type: "IS_ERROR",
      payload: error?.response?.data?.message || "Failed to create cart",
    });
  }
};

export const getUserCart = () => async (dispatch, getState) => {
  try {
    dispatch({
      type: "IS_FETCHING",
    });
    const { data } = await api.get('/carts/users/cart');
    dispatch({
      type: "GET_CART_PRODUCTS",
      payload: data.products,
      totalPrice: data.totalPrice,
      cartId: data.cartId
    })
    localStorage.setItem("cartItems", JSON.stringify(getState().carts.cart));
    dispatch({ type: "IS_SUCCESS" });
  } catch (error) {
    console.log(error);
    dispatch({
      type: "IS_ERROR",
      payload: error?.response?.data?.message || "Failed to fetch cart items",
    });
  }
};

export const createStripePaymentSecret = (totalPrice) => async (dispatch, getState) => {
  try {
    dispatch({ type: "IS_FETCHING" });
    const { data } = await api.post("/order/stripe-client-secret", {
      "amount": Number(totalPrice) * 100,
      "currency": "usd",
    });
    dispatch({ type: "CLIENT_SECRET", payload: data });
    localStorage.setItem("client-secret", JSON.stringify(data));
    dispatch({ type: "IS_SUCCESS" });
  } catch (error) {
    console.log(error);
    toast.error(error?.response?.data?.message || "Failed to create client secret!")
  }
}

export const stripePaymentConfirmation = (sendData, setError, setIsLoading, toast) => async (dispatch, getState) => {
  try {
    const response = await api.post("/order/users/payments/online", sendData);
    if (response.data) {
      localStorage.removeItem("cartItems");
      localStorage.removeItem("CHECKOUT_ADDRESS");
      localStorage.removeItem("client-secret");
      dispatch({
        type: "REMOVE_CLIENT_SECRET_ADDRESS"
      });
      dispatch({ type: "CLEAR_CART" });
      toast.success("Order accepted!");
    } else {
      setError("Payment failed, please try again in some time.")
    }
  } catch (error) {
    setError("Payment failed, please try again in some time");
  }
}