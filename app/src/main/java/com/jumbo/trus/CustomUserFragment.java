package com.jumbo.trus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.listener.OnListListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.user.login.LoginActivity;
import com.jumbo.trus.user.User;

import java.util.ArrayList;
import java.util.List;

public class CustomUserFragment extends Fragment implements OnListListener {
    private static final String TAG = "CustomUserFragment";
    protected User user;
    protected SharedViewModel sharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasOpenedDialogs(getActivity())) {
                    requireActivity().onBackPressed();
                }
                else {
                    openPreviousFragment();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSharedViewModel();
    }

    private boolean hasOpenedDialogs(FragmentActivity activity) {
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).dismiss();
                return true;
            }
        }
        return false;
    }

    /**
     * hod?? usera do fieldu user a z??rove?? zavol?? metody pro skryt?? tla????tek a vyhozen?? denied usera
     */
    private void initSharedViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setUser(sharedViewModel.getUser().getValue());
        setUserPermissions(user);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: zmena uzivatele " + user);
                setUser(user);
                setUserPermissions(user);

            }
        });
    }

    private void setUserPermissions(User user) {
        hideAllButtonsForReadOnlyUsers(getAllViewsFromViewGroup((ViewGroup) getView()));
        checkIfUserIsNotDenied(user);
    }

    /** zjist?? jestli u??ivatel neni ve statusu Denied. Pokud ano, tak ho odhl??s?? a vytvo???? hl????ku
     * @param user
     */
    protected void checkIfUserIsNotDenied(User user) {
        if (user.getStatus() == User.Status.DENIED) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra("logout", true);
            startActivity(intent);
            Toast.makeText(getActivity(), "Vypadni ??m??r??ku", Toast.LENGTH_LONG).show();
        }
    }


    protected void createNotification(Notification notification, INotificationSender iNotificationSender) {
        notification.setUser(user);
        iNotificationSender.sendNotificationToRepository(notification);
    }

    protected void setUser(User user) {
        this.user = user;
    }

    private boolean isUserPermissionReadOnly() {
        if (user != null) {
            return user.getPermission() == User.Permission.READ_ONLY;
        }
        return false;
    }

    protected void proceedToNextFragment(int fragmentNumber) {
        ((MainActivity) getActivity()).addNewPreviousFragment();
        ((MainActivity) getActivity()).replaceFragments(fragmentNumber);
    }

    protected void reloadFragment() {
        ((MainActivity) getActivity()).reloadFragment();
    }

    protected void openPreviousFragment() {
        Log.d(TAG, "openPreviousFragment: counter" );
        ((MainActivity) getActivity()).openPreviousFragment();
    }

    /*protected void setPickedMatch(Match match) {
        ((MainActivity) getActivity()).setPickedMatch(match);
    }

    protected void setMainMatch(Match match) {
        ((MainActivity) getActivity()).setPickedMatch(match);
        ((MainActivity) getActivity()).setMatch(match);
    }

    protected Match getMainMatch() {
        return ((MainActivity) getActivity()).getPickedMatchFromDB();
    }*/

    /** proleze seznam views a zakryje ve??ker?? buttony a imagebuttony pro usery s pr??vy read_only
     * @param views seznam views u kter?? chceme skr??t
     * @return true pokud je user ReadOnly, jinak false
     */
    private boolean hideAllButtonsForReadOnlyUsers(List<View> views) {
        if (isUserPermissionReadOnly()) {
            for (View view : views) {
                if (view instanceof Button || view instanceof ImageButton) {
                    view.setVisibility(View.INVISIBLE);
                }
            }
            return true;
        }
        else {
            for (View view : views) {
                if (view instanceof Button || view instanceof ImageButton) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
        return false;
    }

    /**
     * @param viewGroup ViewGroup fragmentu
     * @return list v??ech views fragmentu
     */
    private List<View> getAllViewsFromViewGroup(ViewGroup viewGroup) {
        List<View> viewList = new ArrayList<>();
        for (int i=0; i<viewGroup.getChildCount(); i++) {
            viewList.add(viewGroup.getChildAt(i));
        }
        return viewList;
    }

    @Override
    public void onItemClick(int position) {
        if (!isUserPermissionReadOnly()) {
            itemClick(position);
        }
        else {
            clickWithoutPermission();
        }
    }

    @Override
    public void onItemLongClick(int position) {
        if (!isUserPermissionReadOnly()) {
            itemLongClick(position);
        }
        else {
            clickWithoutPermission();
        }
    }

    /**metoda se provol?? kdy po provol??n?? onItemClick, pokud m?? u??ivatel pravomoci
     * @param position pozice na kterou se klikne v RecycleView (u kter?? se provol?? onItemClick)
     */
    protected void itemClick(int position) {
    }

    /**metoda se provol?? kdy po provol??n?? onItemLongClick, pokud m?? u??ivatel pravomoci
     * @param position pozice na kterou se dlouze klikne v RecycleView (u kter?? se provol?? onItemLongClick)
     */
    protected void itemLongClick(int position) {
    }

    private void clickWithoutPermission() {
        Toast.makeText(getActivity(), "Na editaci t??to polo??ky ti chyb?? pravomoci!", Toast.LENGTH_SHORT).show();
    }
}
