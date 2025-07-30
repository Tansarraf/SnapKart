// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API,
  authDomain: "e-commerce-web-project-542c6.firebaseapp.com",
  projectId: "e-commerce-web-project-542c6",
  storageBucket: "e-commerce-web-project-542c6.firebasestorage.app",
  messagingSenderId: "47052920688",
  appId: "1:47052920688:web:742df56d8130eff6a361b5",
  measurementId: "G-HQ8WZNNRVT",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const provider = new GoogleAuthProvider();
