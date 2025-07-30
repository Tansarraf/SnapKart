// Configuration for custom API object
import axios from "axios";

// Useful over hardcoding the base url because - we can directly use the api object, any change in api can be done in env file and in base url we can change in object no need to change everywhere hence we gain more control with a "centralized configuration"
const api = axios.create({
  baseURL: `${import.meta.env.VITE_BACKEND_URL}/api`,
  withCredentials:true,
});
export default api;
