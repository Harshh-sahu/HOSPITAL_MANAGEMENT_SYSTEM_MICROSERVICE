import { createSlice } from "@reduxjs/toolkit";
import { jwtDecode } from "jwt-decode";

const profileSlice = createSlice({

    name: "profile",
    initialState: {},
    reducers: {
        setProfile: (state, action) => {
     state = action.payload;
            return state;
        },
        removeProfile: (state) => {

            state = {};
            return state;
        }
    }

})


export const {removeProfile ,setProfile} = profileSlice.actions;
export default profileSlice.reducer;