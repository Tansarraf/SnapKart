import { Avatar, Menu, MenuItem } from "@mui/material";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { BiLogOut, BiUser } from "react-icons/bi";
import { useDispatch, useSelector } from "react-redux";
import { FaShoppingCart } from "react-icons/fa";
import BackDrop from "./BackDrop";
import { logOutUser } from "../store/actions";

const UserMenu = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);
  const { user } = useSelector((state) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const logOutHandler = () => {
    dispatch(logOutUser(navigate));
  };
  console.log(user);
  return (
    <div className="relative z-30">
      <div
        className="sm:border-[1px] sm:border-slate-400 flex flex-row items-center gap-1 rounded-full cursor-pointer hover:shadow-md transition text-slate-700"
        onClick={handleClick}
      >
        <Avatar
          alt="User"
          src={user?.photoURL ?? ""}
          key={user?.photoURL}
          imgProps={{ referrerPolicy: "no-referrer" }}
        />
      </div>
      <Menu
        sx={{ width: "600px" }}
        id="basic-menu"
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        slotProps={{
          list: {
            "aria-labelledby": "basic-button",
            sx: { width: 180 },
          },
        }}
      >
        <Link to="/profile">
          <MenuItem className="flex gap-2" onClick={handleClose}>
            <BiUser className="text-xl" />
            <span className="font-bold text-[16px] mt-1">
              {user?.displayName || user?.username}
            </span>
          </MenuItem>
        </Link>

        <Link to="/profile/orders">
          <MenuItem className="flex gap-2" onClick={handleClose}>
            <FaShoppingCart className="text-xl" />
            <span className="font-semibold">Order</span>
          </MenuItem>
        </Link>

        <MenuItem className="flex gap-2" onClick={logOutHandler}>
          <div className="font-semibold w-full flex gap-2 items-center px-4 py-1 text-white rounded-sm bg-gradient-to-r from-purple-600 to-red-500 ">
            <BiLogOut className="text-xl" />
            <span className="font-semibold">Log Out</span>
          </div>
        </MenuItem>
      </Menu>
      {open && <BackDrop />}
    </div>
  );
};

export default UserMenu;
