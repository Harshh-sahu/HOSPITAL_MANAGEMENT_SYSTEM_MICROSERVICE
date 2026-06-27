import { createSlice } from "@reduxjs/toolkit";
import { jwtDecode } from "jwt-decode";

const isTokenValid = (token: string | null): boolean => {
    if (!token) return false;
    try {
        const { exp } = jwtDecode<{ exp: number }>(token);
        return exp * 1000 > Date.now();
    } catch {
        return false;
    }
};

const storedToken = localStorage.getItem("token");
if (!isTokenValid(storedToken)) {
    localStorage.removeItem("token");
}

const jwtSlice = createSlice({
    name: "jwt",
    initialState: isTokenValid(storedToken) ? storedToken! : '',
    reducers: {
        setJwt: (state, action) => {
            localStorage.setItem("token", action.payload);
            state = action.payload;
            return state;
        },
        removeJwt: (state) => {
            localStorage.removeItem("token");
            state = '';
            return state;
        }
    }
})

export const { setJwt, removeJwt } = jwtSlice.actions;
export { isTokenValid };
export default jwtSlice.reducer;