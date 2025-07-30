import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import InputField from "../Shared/InputField";
import { FaUserPlus } from "react-icons/fa";
import { useDispatch } from "react-redux";
import { registerNewUser } from "../../store/actions";
import toast from "react-hot-toast";
import Spinner from "../Shared/Spinner";
import GoogleLogin from "../Shared/GoogleLogin";

const Register = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [loader, setLoader] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ mode: "onTouched" });

  const registerHandler = async (data) => {
    dispatch(registerNewUser(data, toast, reset, navigate, setLoader));
  };

  return (
    <div className="min-h-[calc(100vh-64px)] flex justify-center items-center">
      <form
        onSubmit={handleSubmit(registerHandler)}
        className="sm:w-[450px] w-[360px] shadow-[0_0_15px_rgba(0,0,0,0.3)] py-6 sm:px-8 px-4 rounded-md"
      >
        <div className="flex flex-col items-center justify-center space-y-4">
          <FaUserPlus className="text-slate-800 text-5xl" />
          <h1 className="text-slate-800 text-center lg:text-3xl text-2xl font-bold">
            Register
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
            label="Email"
            required
            id="email"
            type="email"
            message="*Enter your email"
            placeholder="johndoe@xyz.com"
            register={register}
            errors={errors}
          />
          <InputField
            label="Password"
            required
            id="password"
            type="password"
            min={8}
            message="*Password is required"
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
              (Loading...)
            </>
          ) : (
            <>Register</>
          )}
        </button>
        <GoogleLogin text="Sign Up with Google" />
        <p className="text-center text-sm text-slate-700 mt-4 ">
          Already have an account?{" "}
          <Link
            to="/login"
            className="font-semibold underline hover:text-black"
          >
            <span>Login</span>
          </Link>
        </p>
      </form>
    </div>
  );
};

export default Register;
