import { useState } from "react";
import { useForm } from "react-hook-form";
import { MdOutlineLogin } from "react-icons/md";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../Shared/InputField";
import { useDispatch } from "react-redux";
import { authSigninUser } from "../../store/actions";
import toast from "react-hot-toast";
import Spinner from "../Shared/Spinner";
import GoogleLogin from "../Shared/GoogleLogin";

const Login = () => {
  const navigate = useNavigate();
  const [loader, setLoader] = useState(false);
  const dispatch = useDispatch();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ mode: "onTouched" });

  const loginHandler = async (data) => {
    dispatch(authSigninUser(data, toast, reset, navigate, setLoader));
  };

  return (
    <div className="min-h-[calc(100vh-64px)] flex justify-center items-center">
      <form
        onSubmit={handleSubmit(loginHandler)}
        className="sm:w-[450px] w-[360px] shadow-[0_0_15px_rgba(0,0,0,0.3)] py-8 sm:px-8 px-4 rounded-md"
      >
        <div className="flex flex-col items-center justify-center space-y-4">
          <MdOutlineLogin className="text-slate-800 text-5xl" />
          <h1 className="text-slate-800 text-center lg:text-3xl text-2xl font-bold">
            Login
          </h1>
        </div>
        <hr className="mt-2 mb-5 text-slate-400" />
        <div className="flex flex-col gap-3">
          <InputField
            label="User Name"
            required
            id="username"
            type="text"
            message="*Enter a name"
            placeholder="e.g. John Doe"
            register={register}
            errors={errors}
          />
          <InputField
            label="Password"
            required
            id="password"
            type="password"
            message="*Enter a password"
            placeholder="Enter your password"
            register={register}
            errors={errors}
          />
        </div>
        <button
          className="bg-[linear-gradient(to_right,_#7e22ce,_#ef4444)] flex gap-2 items-center justify-center font-semibold text-white w-full py-2 hover:cursor-pointer hover:text-slate-400 transition-colors duration-100 rounded-sm my-3"
          disabled={loader}
          type="submit"
        >
          {loader ? (
            <>
              <Spinner />
              Loading...
            </>
          ) : (
            <>Login</>
          )}
        </button>

        <GoogleLogin />

        <p className="text-center text-sm text-slate-700 mt-6 ">
          Don't have an account?{" "}
          <Link
            to="/signup"
            className="font-semibold underline hover:text-black"
          >
            <span>Sign Up</span>
          </Link>
        </p>
      </form>
    </div>
  );
};

export default Login;
