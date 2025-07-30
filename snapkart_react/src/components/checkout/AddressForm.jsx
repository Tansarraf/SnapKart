import { MdOutlineLogin } from "react-icons/md";
import InputField from "../Shared/InputField";
import Spinner from "../Shared/Spinner";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { FaAddressCard } from "react-icons/fa6";
import { addOrUpdateAddress } from "../../store/actions";
import toast from "react-hot-toast";

const AddressForm = ({ address, setOpenAddressModal }) => {
  const navigate = useNavigate();
  const { buttonLoader } = useSelector((state) => state.errors);
  const dispatch = useDispatch();

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm({ mode: "onTouched" });

  const onSaveAddressHandler = async (data) => {
    dispatch(
      addOrUpdateAddress(data, toast, address?.addressId, setOpenAddressModal)
    );
  };

  useEffect(() => {
    if (address?.addressId) {
      setValue("buildingName", address?.buildingName);
      setValue("city", address?.city);
      setValue("street", address?.street);
      setValue("state", address?.state);
      setValue("pincode", address?.pincode);
      setValue("country", address?.country);
    }
  }, [address]);
  return (
    <div className="">
      <form onSubmit={handleSubmit(onSaveAddressHandler)} className="">
        <div className="flex justify-center items-center mb-4 font-semibold text-2xl text-slate-800 py-2 px-4">
          <FaAddressCard className="text-4xl mr-3" />
          <h1 className="text-slate-800 text-center lg:text-3xl text-2xl font-bold">
            {!address?.addressId ? "Add Address" : "Update Address"}
          </h1>
        </div>
        <div className="flex flex-col gap-4">
          <InputField
            label="Apartment Name"
            required
            id="buildingName"
            type="text"
            message="*Apartment name is required"
            placeholder="Enter your apartment name"
            register={register}
            errors={errors}
          />
          <InputField
            label="City"
            required
            id="city"
            type="text"
            message="*City is required"
            placeholder="Enter your city"
            register={register}
            errors={errors}
          />
          <InputField
            label="State"
            required
            id="state"
            type="text"
            message="*State is required"
            placeholder="Enter your state"
            register={register}
            errors={errors}
          />
          <InputField
            label="Pincode"
            required
            id="pincode"
            type="text"
            message="*Pincode is required"
            placeholder="Enter your pincode"
            register={register}
            errors={errors}
          />
          <InputField
            label="Street"
            required
            id="street"
            type="text"
            message="*Street is required"
            placeholder="Enter your street"
            register={register}
            errors={errors}
          />
          <InputField
            label="Country"
            required
            id="country"
            type="text"
            message="*Country is required"
            placeholder="Enter your country"
            register={register}
            errors={errors}
          />
        </div>
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded-md mt-4 hover:cursor-pointer hover:bg-blue-700 transition-all"
          disabled={buttonLoader}
          type="submit"
        >
          {buttonLoader ? (
            <>
              <Spinner />
              Loading...
            </>
          ) : (
            <>Save</>
          )}
        </button>
      </form>
    </div>
  );
};

export default AddressForm;
