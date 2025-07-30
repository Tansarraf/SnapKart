import { GoogleAuthProvider, signInWithPopup } from "firebase/auth";
import { auth, provider } from "../../config/firebase";
import { FcGoogle } from "react-icons/fc";
import toast from "react-hot-toast";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { authGoogleLoginUser } from "../../store/actions";
import Spinner from "./Spinner";

const GoogleLogin = ({ text = "Continue with Google" }) => {
  const provider = new GoogleAuthProvider();
  provider.setCustomParameters({
    prompt: "select_account",
  });
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [loader, setLoader] = useState(false);

  const handleGoogleLogin = async () => {
    try {
      const result = await signInWithPopup(auth, provider);
      const user = result.user;
      const token = await user.getIdToken();
      const displayName = user.displayName;
      const photoURL = user.photoURL;

      console.log("✅ Google user:", user);
      console.log("🪪 Firebase ID Token:", token);
      dispatch(
        authGoogleLoginUser(
          token,
          photoURL,
          displayName,
          toast,
          navigate,
          setLoader
        )
      );
    } catch (err) {
      console.error("❌ Google Login Error:", err);
      toast.error("Google login failed. Try again.");
    }
  };

  return (
    <button
      onClick={handleGoogleLogin}
      type="button"
      className={`flex items-center justify-center gap-2 w-full py-2 border cursor-pointer border-gray-300 rounded-md text-slate-800 font-semibold transition-colors duration-200 my-3 ${
        loader ? "opacity-50 cursor-not-allowed" : "hover:bg-gray-100"
      }`}
    >
      {loader ? (
        <>
          <Spinner />
          Logging in...
        </>
      ) : (
        <>
          <FcGoogle size={20} />
          {text}
        </>
      )}
    </button>
  );
};

export default GoogleLogin;
