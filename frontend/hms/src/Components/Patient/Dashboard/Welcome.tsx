import { Avatar } from "@mantine/core";
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { getUserProfile } from "../../../Service/UserService";
import useProtectedImage from "../../../Utility/useProtectedImage";

const Welcome = () => {
  const user = useSelector((state: any) => state.user);
  const [picId,setPicId]=useState<string | null>(null);

   useEffect(()=>{
    
      if(!user)return;
getUserProfile(user.id).then((res)=>{
  setPicId(res);
}).catch((err)=>{
  console.error("Error fetching user profile:", err);
});},[]);

const url = useProtectedImage(picId);

  return (
    <div className="p-5 border shadow-sm rounded-xl bg-blue-50 flex flex-col gap-3">
      <div className="flex justify-between items-center">
        <div>
          <div className="font-extrabold">Welcome Back</div>
          <div className="text-3xl font-semibold text-blue-600" >{user?.name} !</div>
          <div className="text-sm">A+, India</div>
        </div>
        <Avatar src={url} size={100} alt="it's me" radius="xl" />
      </div>
      <div className="gap-3 flex">
            <div className="p-3 rounded-xl bg-violet-200">
                <div className="text-sm">Visits</div>
                <div className="text-lg font-semibold text-violet-600">120+</div>

            </div>
              <div className="p-3 rounded-xl bg-orange-200">
                <div className="text-sm">Medications</div>
                <div className="text-lg font-semibold text-orange-600">80+</div>

            </div>
      </div>
    </div>
  );
};

export default Welcome;
