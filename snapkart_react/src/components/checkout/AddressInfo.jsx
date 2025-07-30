import { useState } from "react";
import Skeleton from "../Shared/Skeleton";
import { FaRegAddressCard } from "react-icons/fa6";
import AddressInfoModal from "./AddressInfoModal";
import AddressForm from "./AddressForm";
import { useDispatch, useSelector } from "react-redux";
import AddressList from "./AddressList";
import { DeleteModal } from "./DeleteModal";
import toast from "react-hot-toast";
import { deleteUserAddress } from "../../store/actions";

const AddressInfo = ({ address }) => {
  const noAddressExist = !address || address.length === 0;
  const { isLoading, buttonLoader } = useSelector((state) => state.errors);

  const [openAddressModal, setOpenAddressModal] = useState(false);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [selectedAddress, setSelectedAddress] = useState("");

  const dispatch = useDispatch();

  const addNewAddressHandler = () => {
    setSelectedAddress("");
    setOpenAddressModal(true);
  };

  const deleteAddressHandler = () => {
    dispatch(
      deleteUserAddress(toast, selectedAddress?.addressId, setOpenDeleteModal)
    );
  };

  return (
    <div className="pt-4">
      {noAddressExist ? (
        <div className="p-6 rounded-lg max-w-md mx-auto flex flex-col items-center justify-center">
          <FaRegAddressCard size={50} className="text-gray-500 mb-4" />
          <h1 className="mb-2 text-slate-900 font-semibold text-center text-2xl">
            No address added yet
          </h1>
          <h1 className="mb-6 text-slate-800 text-center">
            Please add your address to complete the purchase
          </h1>
          <button
            className="px-4 py-2 bg-blue-600 text-white font-medium rounded hover:cursor-pointer hover:bg-blue-700 transition-all"
            onClick={addNewAddressHandler}
          >
            Add Address
          </button>
        </div>
      ) : (
        <div className="relative p-6 rounded-lg max-w-md mx-auto">
          <h1 className="text-slate-800 font-bold text-center text-2xl">
            Select Address
          </h1>
          {isLoading ? (
            <div className="py-4 px-3">
              <Skeleton />
            </div>
          ) : (
            <>
              <div className="space-y-4 pt-6">
                <AddressList
                  addresses={address}
                  setSelectedAddress={setSelectedAddress}
                  setOpenAddressModal={setOpenAddressModal}
                  setOpenDeleteModal={setOpenDeleteModal}
                />
              </div>
              {address.length > 0 && (
                <div className="mt-4">
                  <button
                    className="px-4 py-2 bg-blue-600 text-white font-medium rounded hover:cursor-pointer hover:bg-blue-700 transition-all"
                    onClick={addNewAddressHandler}
                  >
                    Add More
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      )}
      <AddressInfoModal open={openAddressModal} setOpen={setOpenAddressModal}>
        <AddressForm
          address={selectedAddress}
          setOpenAddressModal={setOpenAddressModal}
        />
      </AddressInfoModal>

      <DeleteModal
        open={openDeleteModal}
        loader={buttonLoader}
        setOpen={setOpenDeleteModal}
        title="Delete Address"
        onDeleteHandler={deleteAddressHandler}
      />
    </div>
  );
};

export default AddressInfo;
