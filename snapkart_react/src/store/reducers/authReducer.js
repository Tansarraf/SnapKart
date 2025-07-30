import { selectUserCheckoutAddress } from "../actions";

const initialState = {
  user: null,
  token: localStorage.getItem("authToken") || null,
  address: [],
  clientSecret: null,
  selectedUserCheckoutAddress: null,
};

export const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case "LOGIN_USER":
    case "AUTH_USER":
      localStorage.setItem("user", JSON.stringify(action.payload.user));
      localStorage.setItem("authToken", action.payload.token);
      return {
        ...state,
        user: action.payload.user || action.payload,
        token: action.payload.token,
      };
    case "REMOVE_CHECKOUT_ADDRESS":
      return { ...state, selectedUserCheckoutAddress: null };
    case "CLIENT_SECRET":
      return { ...state, clientSecret: action.payload };
    case "REMOVE_CLIENT_SECRET_ADDRESS":
      return {
        ...state, clientSecret: null, selectUserCheckoutAddress: null
      }

    case "LOG_OUT":
      localStorage.removeItem("user");
      localStorage.removeItem("authToken");
      return { user: null, address: null, token: null };
    case "USER_ADDRESS":
      return { ...state, address: action.payload }
    case "SELECT_CHECKOUT_ADDRESS":
      return { ...state, selectedUserCheckoutAddress: action.payload }
    default:
      return state;
  }
};
